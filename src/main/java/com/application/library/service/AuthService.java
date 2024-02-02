package com.application.library.service;

import com.application.library.converter.UserConverter;
import com.application.library.data.dto.user.LoginRequestDto;
import com.application.library.data.dto.user.LoginResponseDto;
import com.application.library.model.User;
import com.application.library.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserConverter userConverter;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserConverter userConverter) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userConverter = userConverter;
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));

        if (!authenticate.isAuthenticated()) {
            throw new AuthenticationServiceException("Authentication failed");
        }

        return new LoginResponseDto(
                jwtUtil.generateToken(loginRequestDto.getEmail()),
                userConverter.toBaseDto((User) authenticate.getPrincipal())
        );
    }


}
