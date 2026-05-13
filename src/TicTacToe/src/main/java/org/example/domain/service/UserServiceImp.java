package org.example.domain.service;

import org.example.domain.model.SignUpRequest;
import org.example.domain.model.User;
import org.example.domain.port.PasswordEncoderPort;
import org.example.domain.port.UserRepositoryPort;

import java.util.Optional;
import java.util.UUID;

public record UserServiceImp(UserRepositoryPort userRepository,
                             PasswordEncoderPort passwordEncoder)
        implements UserService {

    @Override
    public Optional<User> getUserLogin(String login) {
        return userRepository.findByUserLogin(login);
    }

    @Override
    public Optional<User> getUserById(UUID userId) {
        return userRepository.findByUserId(userId);
    }

    @Override
    public boolean registration(SignUpRequest request) {
        if (userRepository.existsByUserLogin(request.getLogin())) {
            return false;
        }
        User user = new User();
        user.setUserLogin(request.getLogin());

        String passwordHash = passwordEncoder.encode(request.getPassword());
        user.setUserPasswordHash(passwordHash);
        user.assignDefaultRole();

        userRepository.save(user);

        return true;
    }
}
