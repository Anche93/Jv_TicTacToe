package org.example.web.controller;

import org.example.security.AuthService;
import org.example.web.model.JwtRequest;
import org.example.web.model.JwtResponse;
import org.example.web.model.RefreshJwtRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> userAuthorisation(@RequestBody JwtRequest jwtRequest) {
        try {
            JwtResponse response = authService.login(jwtRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/refresh-access")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshJwtRequest request) {
        try {
            JwtResponse response = authService.refreshAccessToken(request.refreshToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/refresh-refresh")
    public ResponseEntity<?> refreshRefreshToken(@RequestBody RefreshJwtRequest request) {
        try {
            JwtResponse response = authService.refreshRefreshToken(request.refreshToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
