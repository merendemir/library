package com.application.library.data.dto.user;

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
}