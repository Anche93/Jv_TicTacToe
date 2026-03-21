package org.example.web.model;

import java.util.UUID;

public record CurrentGameDto(UUID uuid, GameFieldDto gameField, int firstPlayer) {
}
