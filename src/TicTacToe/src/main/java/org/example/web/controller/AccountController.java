package org.example.web.controller;

import io.jsonwebtoken.Claims;
import org.example.domain.model.SignUpRequest;
import org.example.domain.model.User;
import org.example.domain.service.UserService;
import org.example.security.JwtProvider;
import org.example.web.mapper.UserWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class AccountController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    public AccountController(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/reg")
    public ResponseEntity<?> userRegistration(@RequestBody SignUpRequest request) {
        if (userService.registration(request)) {
            Optional<User> optionalUser = userService.getUserLogin(request.getLogin());
            return optionalUser.map(user -> ResponseEntity.status(HttpStatus.CREATED)
                            .body(UserWebMapper.toDto(user)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Ошибка при регистрации! Пользователь уже существует.");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserInfoByToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Claims claims = jwtProvider.extractAllClaims(token);
            UUID userId = UUID.fromString(claims.getSubject());
            Optional<User> optionalUser = userService.getUserById(userId);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Пользователь не найден!");
            }
            return ResponseEntity.ok(UserWebMapper.toDto(optionalUser.get()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Невалидный токен");
        }
    }
}
