package org.example.domain.port;

import org.example.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    Optional<User> findByUserLogin(String login);

    Optional<User> findByUserId(UUID userId);

    void save(User user);

    boolean existsByUserLogin(String login);
}
