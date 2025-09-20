package com.library.bookclub.enums;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

public enum UserType implements GrantedAuthority  {
    SUPERUSER,
    ADMIN,
    EMPLOYEE;

    @Override
    public String getAuthority() {
        return this.name();
    }
}