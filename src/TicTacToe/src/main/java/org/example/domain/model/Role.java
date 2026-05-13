package org.example.domain.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER;

    public String getName() {
        return this.name();
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
