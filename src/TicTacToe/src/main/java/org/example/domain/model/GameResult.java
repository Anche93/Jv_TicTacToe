package org.example.domain.model;

import java.util.UUID;

public class GameResult {
    private ModelCurrentGame currentGame;
    private GameStatus status;
    private String infoMessage;
    private UUID playerId;

    public GameResult(ModelCurrentGame currentGame, GameStatus status, String infoMessage) {
        this.currentGame = currentGame;
        this.status = status;
        this.infoMessage = infoMessage;
    }

    public GameResult(ModelCurrentGame currentGame, GameStatus status, String infoMessage, UUID playerId) {
        this.currentGame = currentGame;
        this.status = status;
        this.infoMessage = infoMessage;
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public ModelCurrentGame getCurrentGame() {
        return currentGame;
    }

    public GameStatus getStatus() {
        return status;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public static GameResult playerWins(ModelCurrentGame currentGame) {
        return new GameResult(currentGame, GameStatus.PLAYER_WIN, "Вы победили!");
    }

    public static GameResult playerWins(ModelCurrentGame currentGame, UUID playerId) {
        return new GameResult(currentGame, GameStatus.PLAYER_WIN, "Игрок с ID: " + playerId + " победил!", playerId);
    }

    public static GameResult computerWins(ModelCurrentGame currentGame) {
        return new GameResult(currentGame, GameStatus.COMPUTER_WIN, "Компьютер победил!");
    }

    public static GameResult zeroWins(ModelCurrentGame currentGame) {
        return new GameResult(currentGame, GameStatus.ZERO_WIN, "Ничья!");
    }

    public static GameResult continuesGame(ModelCurrentGame currentGame) {
        return new GameResult(currentGame, GameStatus.GAME_CONTINUES, "Ваш ход");
    }

    public static GameResult continuesGame(ModelCurrentGame currentGame, UUID playerId) {
        return new GameResult(currentGame, GameStatus.GAME_CONTINUES, "Ход переходит игроку с ID: " + playerId, playerId);
    }

    public static GameResult invalidMove(ModelCurrentGame currentGame, String reason) {
        return new GameResult(currentGame, GameStatus.INVALID_MOVE, reason);
    }

    public static GameResult gameIsEnd(ModelCurrentGame currentGame) {
        return new GameResult(currentGame, GameStatus.GAME_IS_END, "Игра окончена!");
    }

    public static GameResult emptyGame(ModelCurrentGame currentGame) {
        return new GameResult(currentGame, GameStatus.EMPTY_GAME, "Игра не найдена!");
    }
}
