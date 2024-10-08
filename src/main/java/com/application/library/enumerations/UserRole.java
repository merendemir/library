package com.application.library.enumerations;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ROLE_USER,
    ROLE_ADMIN,
    ROLE_LIBRARIAN;

    @Override
    public String getAuthority() {
        return name();
    }
}
