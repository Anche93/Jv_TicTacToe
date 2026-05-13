package org.example.web.model;

public record JwtResponse(String type, String accessToken, String refreshToken) {

    public JwtResponse(String accessToken, String refreshToken) {
        this("Bearer", accessToken, refreshToken);
    }
}
