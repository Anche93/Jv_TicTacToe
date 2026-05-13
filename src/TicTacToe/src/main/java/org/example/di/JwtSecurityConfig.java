package org.example.di;

import io.jsonwebtoken.security.Keys;
import org.example.domain.port.PasswordEncoderPort;
import org.example.domain.service.UserService;
import org.example.security.AuthService;
import org.example.security.JwtProvider;
import org.example.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtSecurityConfig {

    @Value("${jwt.secret}")
    private String secretString;

    @Bean
    public SecretKey jwtSecretKey() {
        return Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    @Bean
    public JwtProvider jwtProvider(SecretKey secretKey) {
        return new JwtProvider(secretKey);
    }

    @Bean
    public AuthService authService(UserService userService,
                                   JwtProvider jwtProvider,
                                   PasswordEncoderPort passwordEncoderPort) {
        return new AuthService(userService, jwtProvider, passwordEncoderPort);
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }
}
