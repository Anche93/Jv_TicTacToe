package org.example.di;

import org.example.domain.port.PasswordEncoderPort;
import org.example.domain.port.UserRepositoryPort;
import org.example.domain.service.UserService;
import org.example.domain.service.UserServiceImp;
import org.example.security.PasswordEncoderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PasswordEncoderPort passwordEncoderPort(PasswordEncoder passwordEncoder) {
        return new PasswordEncoderAdapter(passwordEncoder);
    }

    @Bean
    public UserService userService(UserRepositoryPort userRepositoryPort, PasswordEncoderPort passwordEncoder) {
        return new UserServiceImp(userRepositoryPort, passwordEncoder);
    }
}
