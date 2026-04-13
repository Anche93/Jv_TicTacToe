package org.example.datasource.model;

import jakarta.persistence.*;
import org.example.datasource.converter.MatrixConverter;
import org.example.domain.model.GameStatus;

import java.util.UUID;

@Table(name = "games")
@Entity
public class GameEntity {

    @Id
    @Column(name = "Game_ID")
    private UUID gameUuid;

    @Convert(converter = MatrixConverter.class)
    @Column(name = "Field", columnDefinition = "TEXT")
    private int[][] field;

    @Column(name = "Player_X")
    private UUID idPlayerX;

    @Column(name = "Player_O")
    private UUID idPlayerO;

    @Column(name = "Opponent")
    private String opponent;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private GameStatus gameStatus;

    private int firstPlayer;
    private UUID currentPlayer;

    public GameEntity() {
    }

    public GameEntity(UUID uuid, int[][] field, int firstPlayer) {
        this.gameUuid = uuid;
        this.field = field;
        this.firstPlayer = firstPlayer;
    }

    public GameEntity(UUID gameUuid, int[][] field,
                      UUID idPlayerX, UUID idPlayerO,
                      String opponent,
                      GameStatus gameStatus, int firstPlayer,
                      UUID currentPlayer) {
        this.gameUuid = gameUuid;
        this.field = field;
        this.idPlayerX = idPlayerX;
        this.idPlayerO = idPlayerO;
        this.opponent = opponent;
        this.gameStatus = gameStatus;
        this.firstPlayer = firstPlayer;
        this.currentPlayer = currentPlayer;
    }

    public UUID getGameUuid() {
        return gameUuid;
    }

    public void setGameUuid(UUID uuid) {
        this.gameUuid = uuid;
    }

    public int[][] getField() {
        return field;
    }

    public void setField(int[][] field) {
        this.field = field;
    }

    public int getFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(int firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
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
