package org.example.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public class JwtAuthentication implements Authentication {
        private final Collection<? extends GrantedAuthority> authorities;
        private final UUID principal;
        private boolean authenticated;

    public JwtAuthentication(Collection<? extends GrantedAuthority> authorities,
                             UUID principal,
                             boolean authenticated) {
        this.authorities = authorities;
        this.principal = principal;
        this.authenticated = authenticated;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public UUID getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return principal.toString();
    }
}
