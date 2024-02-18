package com.application.library.service;

import com.application.library.constants.MessageConstants;
import com.application.library.converter.UserConverter;
import com.application.library.data.dto.user.BaseUserSaveRequestDto;
import com.application.library.data.dto.user.UserSaveRequestDto;
import com.application.library.data.view.UserListView;
import com.application.library.data.view.UserView;
import com.application.library.enumerations.UserRole;
import com.application.library.exception.EntityAlreadyExistsException;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.helper.AuthHelper;
import com.application.library.model.User;
import com.application.library.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    public UserService(UserRepository userRepository, UserConverter userConverter) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
    }

    @Transactional
    public User saveBaseUser(BaseUserSaveRequestDto requestDto) {
        if (existsByEmail(requestDto.getEmail())) throw new EntityAlreadyExistsException(MessageConstants.USER_ALREADY_EXISTS_WITH_EMAIL);

        User user = userConverter.toEntity(requestDto);
        user.setAuthorities(Set.of(UserRole.ROLE_USER));
        return userRepository.save(user);
    }

    @Transactional
    public User saveUser(UserSaveRequestDto requestDto) {
        if (existsByEmail(requestDto.getEmail())) throw new EntityAlreadyExistsException(MessageConstants.USER_ALREADY_EXISTS_WITH_EMAIL);
        return userRepository.save(userConverter.toEntity(requestDto));
    }

    @Transactional(readOnly = true)
    public UserView getUserById(Long id) {
        return userRepository.getUserById(id).orElseThrow(() -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<UserListView> getAllUsersByActiveUserAuthority(Optional<UserRole> userType, int page, int size, Optional<String> sortParam, Optional<Sort.Direction> direction) {
        PageRequest pageRequest = getPageRequest(page, size, sortParam, direction);
        if (AuthHelper.isUserAdmin()) {
            return userType.map(userRole ->
                    findAllByAuthorities(userRole, pageRequest))
                    .orElse(userRepository.getAllBy(pageRequest));
        }

        return userRepository.findAllByAuthorities(UserRole.ROLE_USER, pageRequest);
    }

    @Transactional
    public Long deleteUserByActiveUserAuthority(Long id) {
        User deleteUser = findById(id);

        if (!AuthHelper.isUserAdmin()
                && deleteUser.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals(UserRole.ROLE_LIBRARIAN.name()))) {
                throw new AccessDeniedException(MessageConstants.NOT_AUTHORIZED_FOR_DELETE_LIBRARIAN);
        }

        userRepository.delete(deleteUser);
        return id;
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND));
    }

    @Transactional
    public User updateUser(Long userId, UserSaveRequestDto requestDto) {
        User user = findById(userId);
        if (!user.getEmail().equals(requestDto.getEmail()) && existsByEmail(requestDto.getEmail())) throw new EntityAlreadyExistsException(MessageConstants.USER_ALREADY_EXISTS_WITH_EMAIL);
        return userConverter.updateEntity(requestDto, user);
    }

    @Transactional
    public User updateActiveUserInfo(BaseUserSaveRequestDto requestDto) {
        return userRepository.save(userConverter.updateEntity(requestDto, AuthHelper.getActiveUser()));
    }

    private Page<UserListView> findAllByAuthorities(UserRole role, PageRequest pageRequest) {
        return userRepository.findAllByAuthorities(role, pageRequest);
    }

    private PageRequest getPageRequest(int page, int size, Optional<String> sortParam, Optional<Sort.Direction> direction) {
        return sortParam.isPresent() && direction.isPresent() ?
                PageRequest.of(page, size, direction.get(), sortParam.get()) :
                PageRequest.of(page, size, Sort.Direction.ASC, "firstName", "lastName");
    }

    private boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}
