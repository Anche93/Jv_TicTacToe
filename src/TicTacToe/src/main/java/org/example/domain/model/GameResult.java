package org.example.domain.model;

public class GameResult {
    private ModelCurrentGame currentGame;
    private GameStatus status;
    private String infoMessage;

    public enum GameStatus {
        PLAYER_WIN,
        COMPUTER_WIN,
        ZERO_WIN,
        GAME_CONTINUES,
        INVALID_MOVE
    }

    public GameResult(ModelCurrentGame currentGame, GameStatus status, String infoMessage) {
        this.currentGame = currentGame;
        this.status = status;
        this.infoMessage = infoMessage;
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

    public static GameResult computerWins(ModelCurrentGame currentGame) {
        return new GameResult(currentGame, GameStatus.COMPUTER_WIN, "Компьютер победил!");
    }

    public static GameResult zeroWins(ModelCurrentGame currentGame) {
        return new GameResult(currentGame, GameStatus.ZERO_WIN, "Ничья!");
    }

    public static GameResult continuesGame(ModelCurrentGame currentGame) {
        return new GameResult(currentGame, GameStatus.GAME_CONTINUES, "Ваш ход");
    }

    public static GameResult invalidMove(ModelCurrentGame currentGame, String reason) {
        return new GameResult(currentGame, GameStatus.INVALID_MOVE, reason);
    }
}
