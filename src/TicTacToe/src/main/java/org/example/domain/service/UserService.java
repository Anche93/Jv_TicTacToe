package org.example.domain.service;

import org.example.domain.model.SignUpRequest;
import org.example.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Optional<User> getUserLogin(String login);

    Optional<User> getUserById(UUID userId);

    boolean registration(SignUpRequest request);
}
