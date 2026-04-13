package org.example.domain.model;

import java.util.UUID;

public class ModelCurrentGame {

    private UUID uuid;
    private ModelGameField gameField;

    private UUID idPlayerX;
    private UUID idPlayerO;
    private GameStatus gameStatus;
    private UUID currentPlayer;

    private String opponent;
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

    public UUID getIdPlayerX() {
        return idPlayerX;
    }

    public void setIdPlayerX(UUID idPlayerX) {
        this.idPlayerX = idPlayerX;
    }

    public UUID getIdPlayerO() {
        return idPlayerO;
    }

    public void setIdPlayerO(UUID idPlayerO) {
        this.idPlayerO = idPlayerO;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public UUID getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(UUID currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }
}
