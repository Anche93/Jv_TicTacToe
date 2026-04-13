package org.example.web.model;

import org.example.domain.model.GameStatus;

import java.util.UUID;

public record CurrentGameDto(
        UUID uuid,
        GameFieldDto gameField,
        UUID idPlayerX,
        UUID idPlayerO,
        GameStatus gameStatus,
        UUID currentPlayer,
        String opponent,
        int firstPlayer) {
}


