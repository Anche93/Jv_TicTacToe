package org.example.security;

import org.example.domain.port.PasswordEncoderPort;
import org.springframework.security.crypto.password.PasswordEncoder;

public record PasswordEncoderAdapter(PasswordEncoder passwordEncoder) implements PasswordEncoderPort {

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodePassword) {
        return passwordEncoder.matches(rawPassword, encodePassword);
    }
}
