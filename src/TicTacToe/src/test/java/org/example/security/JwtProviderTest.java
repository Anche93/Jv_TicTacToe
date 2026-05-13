package org.example.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.domain.model.Role;
import org.example.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JwtProviderTest {

    private static final String TEST_SECRET = "testSecretKeyForJwtTokenGenerationSpecialLongEnough";
    private static final SecretKey SECRET_KEY =
            Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(SECRET_KEY);
    }

    @Test
    void testGenerationAccessToken_WhenCreatedValidToken() {
        User user = createTestUser();
        String token = jwtProvider.generateAccessToken(user);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);

        Claims claims = jwtProvider.extractAllClaims(token);
        assertEquals(user.getUserId().toString(), claims.getSubject());
        assertNotNull(claims.get("roles", String.class));
    }

    @Test
    void testGenerationRefreshToken_WhenCreatedValidToken() {
        User user = createTestUser();
        String token = jwtProvider.generateAccessToken(user);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);

        Claims claims = jwtProvider.extractAllClaims(token);
        assertEquals(user.getUserId().toString(), claims.getSubject());
    }

    @Test
    void testIsAccessTokenValid_ShouldReturnTrue_WhenValidToken() {
        User user = createTestUser();
        String token = jwtProvider.generateAccessToken(user);
        boolean isValid = jwtProvider.isAccessTokenValid(token, user.getUserId());

        assertTrue(isValid);
    }

    @Test
    void testIsAccessTokenValid_ShouldReturnFalse_WhenUserIdIsWrong() {
        User user = createTestUser();
        String token = jwtProvider.generateAccessToken(user);
        boolean isValid = jwtProvider.isAccessTokenValid(token, UUID.randomUUID());

        assertFalse(isValid);
    }

    @Test
    void testIsAccessTokenValid_ShouldReturnFalse_WhenInvalidToken() {
        String token = "invalid.token.string";
        boolean isValid = jwtProvider.isAccessTokenValid(token, UUID.randomUUID());

        assertFalse(isValid);
    }

    @Test
    void testIsAccessTokenValid_ShouldReturnFalse_WhenExpiredToken() {
        User user = createTestUser();
        String expiredToken = Jwts.builder()
                .subject(user.getUserId().toString())
                .claim("roles", "ROLES_USER")
                .issuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24))
                .expiration(new Date(System.currentTimeMillis() - 1000 * 60))
                .signWith(SECRET_KEY, Jwts.SIG.HS256)
                .compact();
        boolean isValid = jwtProvider.isAccessTokenValid(expiredToken, user.getUserId());
        assertFalse(isValid);
    }

    @Test
    void testIsAccessTokenValid_ShouldReturnFalse_WhenExpiredTokenAndWrongUser() {
        User user = createTestUser();
        String expiredToken = Jwts.builder()
                .subject(user.getUserId().toString())
                .claim("roles", "ROLES_USER")
                .issuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24))
                .expiration(new Date(System.currentTimeMillis() - 1000 * 60))
                .signWith(SECRET_KEY, Jwts.SIG.HS256)
                .compact();
        boolean isValid = jwtProvider.isAccessTokenValid(expiredToken, UUID.randomUUID());
        assertFalse(isValid);
    }

    @Test
    void testIsRefreshTokenValid_ShouldReturnTrue_WhenValidToken() {
        User user = createTestUser();
        String token = jwtProvider.generateRefreshToken(user);
        boolean isValid = jwtProvider.isRefreshTokenValid(token);

        assertTrue(isValid);
    }

    @Test
    void testIsRefreshTokenValid_ShouldReturnFalse_WhenInvalidToken() {
        String token = "invalid.token.string";
        boolean isValid = jwtProvider.isRefreshTokenValid(token);

        assertFalse(isValid);
    }

    @Test
    void testIsRefreshTokenValid_ShouldReturnFalse_WhenExpiredToken() {
        String expiredToken = Jwts.builder()
                .subject(UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 8))
                .expiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24))
                .signWith(SECRET_KEY, Jwts.SIG.HS256)
                .compact();
        boolean isValid = jwtProvider.isRefreshTokenValid(expiredToken);
        assertFalse(isValid);
    }

    @Test
    void testExtractUserId_ShouldReturnCorrectUserId() {
        User user = createTestUser();
        String token = jwtProvider.generateAccessToken(user);
        String extractedUserId = jwtProvider.extractUserId(token);
        assertEquals(user.getUserId().toString(), extractedUserId);
    }

    @Test
    void testExtractRoles_ShouldReturnCorrectRolesString() {
        User user = createTestUser();
        String token = jwtProvider.generateAccessToken(user);
        String roles = jwtProvider.extractRoles(token);
        assertEquals("ROLE_USER", roles);
    }

    @Test
    void testExtractAllClaims_ShouldReturnClaimsWithCorrectData() {
        User user = createTestUser();
        String token = jwtProvider.generateAccessToken(user);
        Claims claims = jwtProvider.extractAllClaims(token);

        assertNotNull(claims);
        assertEquals(user.getUserId().toString(), claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    private User createTestUser() {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setUserLogin("TestUser");
        user.setRoles(List.of(Role.USER));
        return user;
    }
}
