package org.example.security;

import io.jsonwebtoken.Claims;
import org.example.domain.model.User;
import org.example.domain.port.PasswordEncoderPort;
import org.example.domain.service.UserService;
import org.example.web.model.JwtRequest;
import org.example.web.model.JwtResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class AuthService {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoderPort passwordEncoderPort;

    public AuthService(UserService userService,
                       JwtProvider jwtProvider,
                       PasswordEncoderPort passwordEncoderPort) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    public JwtResponse login(JwtRequest jwtRequest) {
        User user = authenticateUser(jwtRequest.login(), jwtRequest.password());
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);
        return new JwtResponse(accessToken, refreshToken);
    }

    public JwtResponse refreshAccessToken(String refreshToken) {
        User user = validateAndGetUserFromRefreshToken(refreshToken);
        String newAccessToken = jwtProvider.generateAccessToken(user);
        return new JwtResponse(newAccessToken, refreshToken);
    }

    public JwtResponse refreshRefreshToken(String refreshToken) {
        User user = validateAndGetUserFromRefreshToken(refreshToken);
        String newAccessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);
        return new JwtResponse(newAccessToken, newRefreshToken);
    }

    public JwtAuthentication getJwtAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthentication jwtAuth) {
            return  jwtAuth;
        }
        return null;
    }

    private User authenticateUser(String login, String password) {
        User user = userService.getUserLogin(login)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден!"));
        if (!passwordEncoderPort.matches(password, user.getUserPasswordHash())) {
            throw new RuntimeException("Неверный логин или пароль!");
        }
        return user;
    }

    private User validateAndGetUserFromRefreshToken(String refreshToken) {
        validateRefreshToken(refreshToken);
        UUID userId = extractUserIdFromToken(refreshToken);
        return findUserById(userId);
    }

    private void validateRefreshToken(String refreshToken) {
        if (!jwtProvider.isRefreshTokenValid(refreshToken)) {
            throw new RuntimeException("Невалидный refresh token");
        }
    }

    private UUID extractUserIdFromToken(String token) {
        Claims claims = jwtProvider.extractAllClaims(token);
        return UUID.fromString(claims.getSubject());
    }

    private User findUserById(UUID userId) {
        return userService.getUserById(userId).orElseThrow(() ->
                new RuntimeException("Пользователь не найден!"));
    }
}
