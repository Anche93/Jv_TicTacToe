package org.example.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
public class ModelCurrentGame {

    private UUID uuid;
    private ModelGameField gameField;

    private UUID idPlayerX;
    private UUID idPlayerO;
    private GameStatus gameStatus;
    private UUID currentPlayer;

    private String opponent;
    private int firstPlayer;

    private LocalDateTime createdAt;

    public ModelCurrentGame() {
        this.uuid = UUID.randomUUID();
        this.gameField = new ModelGameField();
    }

    public ModelCurrentGame(UUID uuid, ModelGameField gameField) {
        this.uuid = uuid;
        this.gameField = gameField;
    }

}
