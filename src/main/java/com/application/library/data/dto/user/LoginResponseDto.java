package com.application.library.data.dto.user;

import java.util.Objects;

public class LoginResponseDto {
    private String authorizationToken;

    private BaseUserDto user;

    public LoginResponseDto() {
    }

    public LoginResponseDto(String authorizationToken, BaseUserDto user) {
        this.authorizationToken = authorizationToken;
        this.user = user;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public BaseUserDto getUser() {
        return user;
    }

    public void setUser(BaseUserDto user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginResponseDto that = (LoginResponseDto) o;
        return Objects.equals(authorizationToken, that.authorizationToken) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorizationToken, user);
    }
}