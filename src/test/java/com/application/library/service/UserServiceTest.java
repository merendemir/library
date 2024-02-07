package com.application.library.service;

import com.application.library.converter.UserConverter;
import com.application.library.data.dto.user.BaseUserSaveRequestDto;
import com.application.library.data.dto.user.UserSaveRequestDto;
import com.application.library.data.view.UserView;
import com.application.library.enumerations.UserRole;
import com.application.library.exception.EntityAlreadyExistsException;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.helper.AuthHelper;
import com.application.library.model.User;
import com.application.library.repository.UserRepository;
import com.application.library.support.TestSupport;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest extends TestSupport {

    private UserRepository userRepository;
    private UserConverter userConverter;
    private UserService userService;

    private static MockedStatic<AuthHelper> authHelperMockedStatic;

    @BeforeAll
    static void beforeAll() {
        authHelperMockedStatic = mockStatic(AuthHelper.class);
    }

    @AfterAll
    static void afterAll() {
        authHelperMockedStatic.close();
    }

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userConverter = mock(UserConverter.class);
        userService = new UserService(userRepository, userConverter);
    }

    @Test
    void testSaveBaseUser_whenSaveBaseUserCalledWithBaseUserSaveRequestDto_shouldReturnUser() {
        // given
        User expectedResult = getTestUser();
        BaseUserSaveRequestDto baseUserSaveRequestDto = new BaseUserSaveRequestDto();
        baseUserSaveRequestDto.setEmail(expectedResult.getEmail());
        baseUserSaveRequestDto.setFirstName(expectedResult.getFirstName());
        baseUserSaveRequestDto.setLastName(expectedResult.getLastName());
        baseUserSaveRequestDto.setPassword(expectedResult.getPassword());

        // when
        when(userRepository.save(any(User.class))).thenReturn(expectedResult);
        when(userConverter.toEntity(baseUserSaveRequestDto)).thenReturn(expectedResult);
        when(userRepository.existsByEmail(expectedResult.getEmail())).thenReturn(false);

        // then
        User result = userService.saveBaseUser(baseUserSaveRequestDto);

        assertEquals(expectedResult, result);

        verify(userRepository, times(1)).save(any(User.class));
        verify(userConverter, times(1)).toEntity(baseUserSaveRequestDto);
        verify(userRepository, times(1)).existsByEmail(expectedResult.getEmail());
    }

    @Test
    void testSaveBaseUser_whenSaveBaseUserWithExistsEmail_shouldThrowEntityAlreadyExistsException() {
        // given
        BaseUserSaveRequestDto requestDto = new BaseUserSaveRequestDto();
        requestDto.setEmail("test_email");

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        // then
        assertThatThrownBy(() -> userService.saveBaseUser(requestDto))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage("User with this email already exists");

        verify(userRepository, times(1)).existsByEmail(requestDto.getEmail());
        verifyNoInteractions(userConverter);
        verifyNoMoreInteractions(userRepository);
    }


    @Test
    void testSaveUser_whenSaveUserCalledWithUserSaveRequestDto_shouldReturnUser() {
        // given
        User expectedResult = getTestUser();
        UserSaveRequestDto userSaveRequestDto = new UserSaveRequestDto();
        userSaveRequestDto.setEmail(expectedResult.getEmail());
        userSaveRequestDto.setFirstName(expectedResult.getFirstName());
        userSaveRequestDto.setLastName(expectedResult.getLastName());
        userSaveRequestDto.setPassword(expectedResult.getPassword());

        // when
        when(userRepository.save(any(User.class))).thenReturn(expectedResult);
        when(userConverter.toEntity(userSaveRequestDto)).thenReturn(expectedResult);

        // then
        User result = userService.saveUser(userSaveRequestDto);

        assertEquals(expectedResult, result);

        verify(userRepository, times(1)).save(any(User.class));
        verify(userConverter, times(1)).toEntity(userSaveRequestDto);
    }

    @Test
    void testSaveUser_whenSaveUserCalledWithExistsEmail_shouldThrowEntityAlreadyExistsException() {
        // given
        UserSaveRequestDto requestDto = new UserSaveRequestDto();
        requestDto.setEmail("test_email");

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        // then
        assertThatThrownBy(() -> userService.saveUser(requestDto))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage("User with this email already exists");

        verify(userRepository, times(1)).existsByEmail(requestDto.getEmail());
        verifyNoInteractions(userConverter);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testGetUserById_whenUserExists_shouldReturnUserView() {
        // given
        User user = getTestUser();
        UserView expected = getTestUserView();

        // when
        when(userRepository.getUserById(user.getId())).thenReturn(Optional.of(expected));

        // then
        UserView result = userService.getUserById(user.getId());

        assertEquals(expected, result);

        verify(userRepository, times(1)).getUserById(user.getId());
    }

    @Test
    void testGetUserById_whenUserNotExists_shouldThrowEntityNotFoundException() {
        // given
        User user = getTestUser();

        // when
        when(userRepository.getUserById(user.getId())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> userService.getUserById(user.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository, times(1)).getUserById(user.getId());
    }

    @Test
    void testGetAllUsersByActiveUserAuthority_whenUserIsAdmin_shouldReturnAllUsers() {
        // given
        int page = 0;
        int size = 10;
        Page<UserView> expected = new PageImpl<>(List.of(getTestUserView()));

        // when
        when(userRepository.getAllBy(any(PageRequest.class))).thenReturn(expected);
        when(AuthHelper.isUserAdmin()).thenReturn(true);

        // then
        Page<UserView> result = userService.getAllUsersByActiveUserAuthority(Optional.empty(), page, size, Optional.empty(), Optional.empty());

        assertEquals(expected, result);

        verify(userRepository, times(1)).getAllBy(any(PageRequest.class));
    }

    @Test
    void testGetAllUsersByActiveUserAuthority_whenUserIsAdminAndUserTypePresent_shouldReturnAllUsers() {
        // given
        int page = 0;
        int size = 10;
        Page<UserView> expected = new PageImpl<>(List.of(getTestUserView()));

        when(userRepository.findAllByAuthorities(any(UserRole.class), any())).thenReturn(expected);
        when(AuthHelper.isUserAdmin()).thenReturn(true);

        // then
        Page<UserView> result = userService.getAllUsersByActiveUserAuthority(Optional.of(UserRole.ROLE_USER), page, size, Optional.of("firstName"), Optional.of(Sort.Direction.ASC));

        assertEquals(expected, result);

        verify(userRepository, times(1)).findAllByAuthorities(any(UserRole.class), any(PageRequest.class));
    }

    @Test
    void testGetAllUsersByActiveUserAuthority_whenUserIsNotAdmin_shouldReturnAllUsersByUserRole() {
        // given
        int page = 0;
        int size = 10;
        Page<UserView> expected = new PageImpl<>(List.of(getTestUserView()));

        // when
        when(userRepository.findAllByAuthorities(any(UserRole.class), any())).thenReturn(expected);
        when(AuthHelper.isUserAdmin()).thenReturn(false);

        // then
        Page<UserView> result = userService.getAllUsersByActiveUserAuthority(Optional.empty(), page, size, Optional.empty(), Optional.empty());

        assertEquals(expected, result);

        verify(userRepository, times(1)).findAllByAuthorities(any(UserRole.class), any(PageRequest.class));
    }

    @Test
    void testDeleteUserByActiveUserAuthority_whenUserIsAdmin_shouldDeleteUser() {
        // given
        User user = getTestUser();

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(AuthHelper.isUserAdmin()).thenReturn(true);

        // then
        Long result = userService.deleteUserByActiveUserAuthority(user.getId());

        assertEquals(user.getId(), result);

        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUserByActiveUserAuthority_whenUserIsNotAdminAndUserIsLibrarian_shouldThrowAccessDeniedException() {
        // given
        User user = getTestUser();
        user.setAuthorities(Set.of(UserRole.ROLE_LIBRARIAN));

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(AuthHelper.isUserAdmin()).thenReturn(false);

        // then
        assertThatThrownBy(() -> userService.deleteUserByActiveUserAuthority(user.getId()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You are not authorized to delete librarian");

        verify(userRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testDeleteUserByActiveUserAuthority_whenUserIsNotAdminAndUserIsNotLibrarian_shouldDeleteUser() {
        // given
        User user = getTestUser();

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(AuthHelper.isUserAdmin()).thenReturn(false);

        // then
        Long result = userService.deleteUserByActiveUserAuthority(user.getId());

        assertEquals(user.getId(), result);

        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testFindById_whenUserExists_shouldReturnUser() {
        // given
        User user = getTestUser();

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // then
        User result = userService.findById(user.getId());

        assertEquals(user, result);

        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void testFindById_whenUserNotExists_shouldThrowEntityNotFoundException() {
        // given
        User user = getTestUser();

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> userService.findById(user.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void testUpdateUser_whenUserExists_shouldReturnUpdatedUser() {
        // given
        User user = getTestUser();
        UserSaveRequestDto requestDto = new UserSaveRequestDto();
        requestDto.setEmail("new_email");

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userConverter.updateEntity(requestDto, user)).thenReturn(user);

        // then
        User result = userService.updateUser(user.getId(), requestDto);

        assertEquals(user, result);

        verify(userRepository, times(1)).findById(user.getId());
        verify(userConverter, times(1)).updateEntity(requestDto, user);
    }

    @Test
    void testUpdateUser_whenUserNotExists_shouldThrowEntityNotFoundException() {
        // given
        User user = getTestUser();
        UserSaveRequestDto requestDto = new UserSaveRequestDto();
        requestDto.setEmail("new_email");

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> userService.updateUser(user.getId(), requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userConverter);
    }

    @Test
    void testUpdateActiveUser_whenUserExists_shouldReturnUpdatedUser() {
        // given
        User user = getTestUser();
        BaseUserSaveRequestDto requestDto = new BaseUserSaveRequestDto();
        requestDto.setEmail("new_email");

        // when
        when(AuthHelper.getActiveUser()).thenReturn(user);
        when(userConverter.updateEntity(requestDto, user)).thenReturn(user);

        // then
        User result = userService.updateActiveUserInfo(requestDto);

        assertEquals(user, result);

        verify(userConverter, times(1)).updateEntity(requestDto, user);
    }

    @Test
    void testUpdateActiveUser_whenUserNotExists_shouldThrowEntityNotFoundException() {
        // given
        User user = getTestUser();
        UserSaveRequestDto requestDto = new UserSaveRequestDto();
        requestDto.setEmail("new_email");

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> userService.updateUser(user.getId(), requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userConverter);
    }

}