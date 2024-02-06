package com.application.library.service;

import com.application.library.converter.UserConverter;
import com.application.library.data.dto.user.BaseUserDto;
import com.application.library.data.dto.user.LoginRequestDto;
import com.application.library.data.dto.user.LoginResponseDto;
import com.application.library.model.User;
import com.application.library.service.impl.UserDetailsServiceImpl;
import com.application.library.support.TestSupport;
import com.application.library.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthServiceTest extends TestSupport {

    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private UserConverter userConverter;

    private AuthService authService;
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtUtil = mock(JwtUtil.class);
        userConverter = mock(UserConverter.class);
        userDetailsService = mock(UserDetailsServiceImpl.class);

        authService = new AuthService(authenticationManager, jwtUtil, userConverter);
    }

    @Test
    void login_whenLoginCalledWithExistsUserAndValidCredentials_shouldReturnLoginResponseDto() {
        // given
        String token = "token";

        User testUser = getTestUser();
        BaseUserDto baseUserDto = getBaseUserDto();
        LoginResponseDto expectedLoginResponseDto = new LoginResponseDto(token, baseUserDto);

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail(testUser.getEmail());
        loginRequestDto.setPassword(testUser.getPassword());
        Authentication testAuthenticationByRole = getTestAuthentication();

        // when
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequestDto.getEmail(), loginRequestDto.getPassword()))).thenReturn(testAuthenticationByRole);
        when(jwtUtil.generateToken(testUser.getEmail())).thenReturn(token);
        when(userConverter.toBaseDto(any())).thenReturn(baseUserDto);
        when(userDetailsService.loadUserByUsername(testUser.getEmail())).thenReturn(testUser);

        // then
        LoginResponseDto result = authService.login(loginRequestDto);

        assertEquals(expectedLoginResponseDto, result);

        verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken(
                loginRequestDto.getEmail(), loginRequestDto.getPassword()));

        verify(jwtUtil, times(1)).generateToken(testUser.getEmail());
        verify(userConverter, times(1)).toBaseDto(testUser);
    }
}