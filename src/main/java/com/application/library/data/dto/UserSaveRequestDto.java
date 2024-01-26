package com.application.library.data.dto;

import com.application.library.model.Role;

import java.util.Set;

public class UserSaveRequestDto extends BaseUserSaveRequestDto {

    Set<Role> roles;

    public Set<Role> getAuthorities() {
        return roles;
    }

    public void setAuthorities(Set<Role> authorities) {
        this.roles = authorities;
    }
}