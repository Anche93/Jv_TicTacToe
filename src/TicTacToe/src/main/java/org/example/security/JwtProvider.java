package org.example.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.domain.model.Role;
import org.example.domain.model.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

public record JwtProvider(SecretKey secretKey) {

    public String generateAccessToken(User user) {
        String roles = user.getRoles()
                .stream()
                .map(Role::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(user.getUserId().toString())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {

        return Jwts.builder()
                .subject(user.getUserId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public boolean isAccessTokenValid(String token, UUID userId) {
        try {
            String extractUserId = extractUserId(token);
            return extractUserId.equals(userId.toString()) && !isTokenExpired(token);
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRoles(String token) {
        return extractAllClaims(token).get("roles", String.class);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
