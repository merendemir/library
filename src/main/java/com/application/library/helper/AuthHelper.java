package com.application.library.helper;

import com.application.library.enumerations.UserRole;
import com.application.library.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Objects;

public class AuthHelper {

    public static User getActiveUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    public static boolean isUserAdmin() {
        return Objects.requireNonNull(getUserAuthorities()).stream().anyMatch(r -> r.getAuthority().equals(UserRole.ROLE_ADMIN.name()));
    }

    public static Collection<? extends GrantedAuthority> getUserAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication == null) ? null : authentication.getAuthorities();
    }
}
