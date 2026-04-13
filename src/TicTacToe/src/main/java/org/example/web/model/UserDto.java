package org.example.web.model;

import java.util.UUID;

public record UserDto(UUID userId, String userLogin) {
}
