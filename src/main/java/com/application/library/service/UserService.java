package com.application.library.service;

import com.application.library.converter.UserConverter;
import com.application.library.data.dto.BaseUserSaveRequestDto;
import com.application.library.data.dto.UserSaveRequestDto;
import com.application.library.data.view.UserView;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.model.Role;
import com.application.library.model.User;
import com.application.library.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        User user = userConverter.toEntity(requestDto);
        user.setAuthorities(Set.of(Role.ROLE_USER));
        return userRepository.save(user);
    }

    @Transactional
    public User saveUser(UserSaveRequestDto requestDto) {
        User user = userConverter.toEntity(requestDto);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserView getUserById(Long id) {
        return userRepository.getUserById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

}
