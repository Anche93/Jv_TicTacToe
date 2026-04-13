package org.example.domain.service;

import org.example.domain.model.SignUpRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface UserService {

    boolean registration(SignUpRequest request);

    UUID authorization(String authHeader);
}
