package org.example.domain.model;

import java.util.UUID;

public class ModelCurrentGame {
    private UUID uuid;
    private ModelGameField gameField;
    private int firstPlayer;

    public ModelCurrentGame() {
        this.uuid = UUID.randomUUID();
        this.gameField = new ModelGameField();
    }

    public ModelCurrentGame(UUID uuid, ModelGameField gameField) {
        this.uuid = uuid;
        this.gameField = gameField;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public ModelGameField getGameField() {
        return gameField;
    }

    public void setGameField(ModelGameField gameField) {
        this.gameField = gameField;
    }

    public int getFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(int firstPlayer) {
        this.firstPlayer = firstPlayer;
    }
}
