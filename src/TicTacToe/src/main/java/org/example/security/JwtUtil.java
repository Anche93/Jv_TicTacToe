package org.example.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;

public class JwtUtil {

    public JwtAuthentication createAuthentication(Claims claims) {
        String userIdString = claims.getSubject();
        UUID userId = UUID.fromString(userIdString);

        String rolesString = claims.get("roles", String.class);
        List<GrantedAuthority> authorities = parseRoles(rolesString);

        return new JwtAuthentication(authorities, userId,true);
    }

    private List<GrantedAuthority> parseRoles(String rolesString) {
        if (rolesString == null || rolesString.isBlank()) {
            return  Collections.emptyList();
        }
        return Arrays
                .stream(rolesString.split(","))
                .map(String::trim)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
