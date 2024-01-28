package com.application.library.data.dto.user;

import com.application.library.enumerations.UserRole;

import java.util.Set;

public class UserSaveRequestDto extends BaseUserSaveRequestDto {

    Set<UserRole> roles;

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }
}