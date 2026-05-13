package org.example.security;

import io.jsonwebtoken.Claims;
import org.example.domain.model.User;
import org.example.domain.port.PasswordEncoderPort;
import org.example.domain.service.UserService;
import org.example.web.model.JwtRequest;
import org.example.web.model.JwtResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private JwtRequest request;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(UUID.randomUUID());
        testUser.setUserLogin("TestUser");
        testUser.setUserPasswordHash("encodedPassword");

        request = new JwtRequest("TestUser", "123password");
    }

    @Test
    void testLogin_ShouldReturnJwtResponse_WhenCredentialsValid() {
        String accessToken = "access.token.forTest";
        String refreshToken = "refresh.token.forTest";

        when(userService.getUserLogin(request.login()))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoderPort.matches(request.password(), testUser.getUserPasswordHash()))
                .thenReturn(true);
        when(jwtProvider.generateAccessToken(testUser)).thenReturn(accessToken);
        when(jwtProvider.generateRefreshToken(testUser)).thenReturn(refreshToken);

        JwtResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals(accessToken, response.accessToken());
        assertEquals(refreshToken, response.refreshToken());

        verify(userService, times(1)).getUserLogin("TestUser");
        verify(passwordEncoderPort, times(1))
                .matches("123password", "encodedPassword");
        verify(jwtProvider, times(1)).generateAccessToken(testUser);
        verify(jwtProvider, times(1)).generateRefreshToken(testUser);
    }

    @Test
    void testLogin_ShouldReturnThrowException_WhenUserNotFound() {
        when(userService.getUserLogin(request.login())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });

        assertEquals("Пользователь не найден!", exception.getMessage());

        verify(passwordEncoderPort, never()).matches(any(), any());
        verify(jwtProvider, never()).generateAccessToken(any());
    }

    @Test
    void testLogin_ShouldReturnThrowException_WhenPasswordIsWrong() {
        when(userService.getUserLogin(request.login())).thenReturn(Optional.of(testUser));
        when(passwordEncoderPort.matches(request.password(), testUser.getUserPasswordHash()))
                .thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });
        assertEquals("Неверный логин или пароль!", exception.getMessage());

        verify(jwtProvider, never()).generateAccessToken(any());
    }

    @Test
    void testRefreshAccessToken_ShouldReturnNewAccessToken_WhenRefreshTokenIsValid() {
        String oldRefreshToken = "valid.refresh.token";
        String newAccessToken = "new.access.token";

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(testUser.getUserId().toString());

        when(jwtProvider.isRefreshTokenValid(oldRefreshToken)).thenReturn(true);
        when(jwtProvider.extractAllClaims(oldRefreshToken)).thenReturn(claims);
        when(userService.getUserById(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(jwtProvider.generateAccessToken(testUser)).thenReturn(newAccessToken);

        JwtResponse response = authService.refreshAccessToken(oldRefreshToken);

        assertNotNull(response);
        assertEquals(newAccessToken, response.accessToken());
        assertEquals(oldRefreshToken, response.refreshToken());

        verify(jwtProvider, times(1)).isRefreshTokenValid(oldRefreshToken);
        verify(jwtProvider, times(1)).generateAccessToken(testUser);
    }

    @Test
    void testRefreshAccessToken_ShouldReturnThrowException_WhenRefreshTokenIsInvalid() {
        String invalidToken = "invalid.token";
        when(jwtProvider.isRefreshTokenValid(invalidToken)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.refreshAccessToken(invalidToken);
        });
        assertEquals("Невалидный refresh token", exception.getMessage());
        verify(jwtProvider, never()).generateAccessToken(any());
    }

    @Test
    void testRefreshAccessToken_ShouldReturnThrowException_WhenUserNotFound() {
        String refreshToken = "valid.refresh.token";
        UUID userId = UUID.randomUUID();

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(userId.toString());

        when(jwtProvider.isRefreshTokenValid(refreshToken)).thenReturn(true);
        when(jwtProvider.extractAllClaims(refreshToken)).thenReturn(claims);
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.refreshAccessToken(refreshToken);
        });

        assertEquals("Пользователь не найден!", exception.getMessage());
    }

    @Test
    void testRefreshRefreshToken_ShouldReturnNewTokens_WhenRefreshTokenIsValid() {
        String oldRefreshToken = "valid.refresh.token";
        String newAccessToken = "new.access.token";
        String newRefreshToken = "new.refresh.token";

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(testUser.getUserId().toString());

        when(jwtProvider.isRefreshTokenValid(oldRefreshToken)).thenReturn(true);
        when(jwtProvider.extractAllClaims(oldRefreshToken)).thenReturn(claims);
        when(userService.getUserById(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(jwtProvider.generateAccessToken(testUser)).thenReturn(newAccessToken);
        when(jwtProvider.generateRefreshToken(testUser)).thenReturn(newRefreshToken);

        JwtResponse response = authService.refreshRefreshToken(oldRefreshToken);

        assertNotNull(response);
        assertEquals(newAccessToken, response.accessToken());
        assertEquals(newRefreshToken, response.refreshToken());

        verify(jwtProvider, times(1)).generateRefreshToken(testUser);
    }

    @Test
    void testRefreshRefreshToken_ShouldReturnThrowException_WhenRefreshTokenIsInvalid() {
        String invalidToken = "invalid.token";
        when(jwtProvider.isRefreshTokenValid(invalidToken)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.refreshRefreshToken(invalidToken);
        });
        assertEquals("Невалидный refresh token", exception.getMessage());
    }

    @Test
    void testJwtAuthentication_ShouldReturnAuthentication_WhenExists() {
        JwtAuthentication expectedAuth = mock(JwtAuthentication.class);
        SecurityContextHolder.getContext().setAuthentication(expectedAuth);

        JwtAuthentication result = authService.getJwtAuthentication();
        assertEquals(expectedAuth, result);

        SecurityContextHolder.createEmptyContext();
    }

    @Test
    void testJwtAuthentication_ShouldReturnNull_WhenNoAuthentication() {
        SecurityContextHolder.clearContext();
        JwtAuthentication result = authService.getJwtAuthentication();
        assertNull(result);
    }

    @Test
    void testJwtAuthentication_ShouldReturnNull_WhenAuthenticationIsWrongType() {
        UsernamePasswordAuthenticationToken otherAuth =
                new UsernamePasswordAuthenticationToken("user", "password");

        SecurityContextHolder.getContext().setAuthentication(otherAuth);

        JwtAuthentication result = authService.getJwtAuthentication();
        assertNull(result);

        SecurityContextHolder.clearContext();
    }
}
