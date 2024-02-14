package com.application.library.data.dto.user;

import com.application.library.enumerations.UserRole;

import java.util.Objects;
import java.util.Set;

public class UserSaveRequestDto extends BaseUserSaveRequestDto {

    Set<UserRole> roles;

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserSaveRequestDto that = (UserSaveRequestDto) o;
        return Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), roles);
    }
}