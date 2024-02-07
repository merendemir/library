package com.application.library.service.impl;

import com.application.library.model.User;
import com.application.library.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    private UserDetailsServiceImpl userDetailsServiceImpl;
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userDetailsServiceImpl = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void testLoadUserByUsername_whenMethodCalledWithExistsUserName_shouldReturnUserDetails() {
        // given
        String username = "test";
        User expectedResult = new User();

        // when
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(expectedResult));

        // then
        UserDetails result = userDetailsServiceImpl.loadUserByUsername(username);
        assertEquals(expectedResult, result);

        verify(userRepository, times(1)).findByEmail(username);
    }

    @Test
    void testLoadUserByUsername_whenMethodCalledWithNotExistsUserName_shouldThrowEntityNotFoundException() {
        // given
        String username = "test";

        assertThatThrownBy(() -> userDetailsServiceImpl.loadUserByUsername(username))
                .isInstanceOf(EntityNotFoundException.class);

        verify(userRepository, times(1)).findByEmail(username);
    }
}