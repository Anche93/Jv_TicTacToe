package org.example.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void testCreateAuthentication_ShouldReturnJwtAuth_WhenValidClaims() {
        Claims claims = mock(Claims.class);
        UUID userId = UUID.randomUUID();
        String rolesString = "ROLE_USER,ROLE_ADMIN";

        when(claims.getSubject()).thenReturn(userId.toString());
        when(claims.get("roles", String.class)).thenReturn(rolesString);

        JwtAuthentication auth = jwtUtil.createAuthentication(claims);

        assertEquals(userId, auth.getPrincipal());
        assertTrue(auth.isAuthenticated());
        assertEquals(2, auth.getAuthorities().size());
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testCreateAuthentication_ShouldReturnEmptyRoles_WhenBlanksString() {
        Claims claims = mock(Claims.class);
        UUID userId = UUID.randomUUID();

        when(claims.getSubject()).thenReturn(userId.toString());
        when(claims.get("roles", String.class)).thenReturn("     ");

        JwtAuthentication auth = jwtUtil.createAuthentication(claims);

        assertEquals(userId, auth.getPrincipal());
        assertTrue(auth.getAuthorities().isEmpty());
    }

    @Test
    void testCreateAuthentication_ShouldReturnEmptyRoles_WhenNullString() {
        Claims claims = mock(Claims.class);
        UUID userId = UUID.randomUUID();

        when(claims.getSubject()).thenReturn(userId.toString());
        when(claims.get("roles", String.class)).thenReturn(null);

        JwtAuthentication auth = jwtUtil.createAuthentication(claims);

        assertEquals(userId, auth.getPrincipal());
        assertTrue(auth.getAuthorities().isEmpty());
    }

    @Test
    void testCreateAuthentication_ShouldReturnSingleRole() {
        Claims claims = mock(Claims.class);
        UUID userId = UUID.randomUUID();

        when(claims.getSubject()).thenReturn(userId.toString());
        when(claims.get("roles", String.class)).thenReturn("ROLE_USER");

        JwtAuthentication auth = jwtUtil.createAuthentication(claims);

        assertEquals(1, auth.getAuthorities().size());
        assertEquals("ROLE_USER", auth.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void testCreateAuthentication_ShouldHandleRolesWithSpaces_WhenParseRoles() {
        Claims claims = mock(Claims.class);
        UUID userId = UUID.randomUUID();

        when(claims.getSubject()).thenReturn(userId.toString());
        when(claims.get("roles", String.class)).thenReturn("ROLE_USER, ROLE_ADMIN, ROLE_MODERATOR");

        JwtAuthentication auth = jwtUtil.createAuthentication(claims);
        assertEquals(3, auth.getAuthorities().size());
        List<String> authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();
        assertTrue(authorities.contains("ROLE_USER"));
        assertTrue(authorities.contains("ROLE_ADMIN"));
        assertTrue(authorities.contains("ROLE_MODERATOR"));
    }
}
