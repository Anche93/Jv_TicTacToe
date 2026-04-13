package org.example.web.controller;

import org.example.datasource.model.UserEntity;
import org.example.datasource.repository.UserRepository;
import org.example.domain.model.SignUpRequest;
import org.example.domain.service.UserService;
import org.example.web.mapper.UserWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class AccountController {

    private final UserRepository userRepository;
    private final UserService userService;

    public AccountController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/reg")
    public ResponseEntity<?> userRegistration(@RequestBody SignUpRequest request) {
        if (userService.registration(request)) {
            Optional<UserEntity> optionalUser = userRepository.findByUserLogin(request.getLogin());
            return optionalUser.map(userEntity -> ResponseEntity.status(HttpStatus.CREATED).body(UserWebMapper.toDto(userEntity)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Ошибка при регистрации! Пользователь уже существует.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> userAuthorisation(@RequestHeader("Authorization") String authHeader) {
        UUID userID = userService.authorization(authHeader);
        if (userID == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ошибка в логине или в пароле!");
        }
        Optional<UserEntity> optionalUser = userRepository.findById(userID);
        return optionalUser.map(userEntity -> ResponseEntity.ok(UserWebMapper.toDto(optionalUser.get())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable UUID userId) {
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(UserWebMapper.toDto(optionalUser.get()));
    }
}
