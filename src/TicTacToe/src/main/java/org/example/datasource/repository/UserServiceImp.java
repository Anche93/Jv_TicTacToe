package org.example.datasource.repository;

import org.example.datasource.model.UserEntity;
import org.example.domain.model.SignUpRequest;
import org.example.domain.model.User;
import org.example.domain.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
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

        UserEntity userEntity = new UserEntity(user.getUserId(), user.getUserLogin(), user.getUserPasswordHash());
        userRepository.save(userEntity);

        return true;
    }

    @Override
    public UUID authorization(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return null;
        }

        String base64Credentials = authHeader.substring(6);
        String credentials = new String(Base64.getDecoder().decode(base64Credentials));
        String[] parts = credentials.split(":", 2);
        if (parts.length != 2) {
            return null;
        }
        String login = parts[0];
        String password = parts[1];

        Optional<UserEntity> optionalUser = userRepository.findByUserLogin(login);
        if (optionalUser.isEmpty()) {
            return null;
        }

        UserEntity userEntity = optionalUser.get();

        if (!passwordEncoder.matches(password, userEntity.getPasswordHash())) {
            return null;
        }
        return userEntity.getUserId();
    }
}
