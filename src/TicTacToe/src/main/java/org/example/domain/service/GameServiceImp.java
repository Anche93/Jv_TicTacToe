package org.example.domain.service;

import org.example.domain.model.*;
import org.example.domain.port.GameRepositoryPort;
import org.example.domain.port.MoveStrategy;

import java.util.*;

public class GameServiceImp implements GameService {

    private final GameRepositoryPort gameRepository;
    private final Random random;
    private final MoveStrategy moveStrategy;
    private final WinChecker winChecker;

    public GameServiceImp(GameRepositoryPort gameRepository,
                          MoveStrategy moveStrategy,
                          WinChecker winChecker) {
        this(gameRepository, new Random(), moveStrategy, winChecker);
    }

    public GameServiceImp(GameRepositoryPort gameRepository,
                          Random random,
                          MoveStrategy moveStrategy,
                          WinChecker winChecker) {
        this.gameRepository = gameRepository;
        this.random = random;
        this.moveStrategy = moveStrategy;
        this.winChecker = winChecker;
    }

    @Override
    public ModelCurrentGame createGameWithComp(UUID playerId) {
        ModelCurrentGame newGame = new ModelCurrentGame();

        int firstPlayer = determineFirstPlayer(newGame);
        newGame.setIdPlayerX(firstPlayer == Constant.PLAYER ? playerId : null);
        newGame.setIdPlayerO(firstPlayer == Constant.COMPUTER ? playerId : null);
        newGame.setOpponent(Constant.COMP);
        newGame.setFirstPlayer(firstPlayer);

        saveWithStatus(newGame, GameStatus.GAME_CONTINUES);
        return newGame;
    }

    @Override
    public ModelCurrentGame createGameWithFriend(UUID playerId) {
        ModelCurrentGame newGame = new ModelCurrentGame();

        newGame.setIdPlayerX(playerId);
        newGame.setIdPlayerO(null);
        newGame.setCurrentPlayer(playerId);
        newGame.setOpponent(Constant.FRIEND);
        newGame.setFirstPlayer(Constant.PLAYER_X);

        saveWithStatus(newGame, GameStatus.WAITING_FOR_PLAYERS);
        return newGame;
    }

    @Override
    public Optional<ModelCurrentGame> getGameById(UUID gameId) {
        return gameRepository.findById(gameId);
    }

    @Override
    public int determineFirstPlayer(ModelCurrentGame currentGame) {
        int firstPlayer = random.nextInt(2) + 1;
        if (firstPlayer == Constant.COMPUTER) {
            makeComputerFirstStep(currentGame);
        }
        return firstPlayer;
    }

    @Override
    public GameResult processMove(ModelCurrentGame currentGame, int row, int col, UUID playerId) {
        if (isGameOver(currentGame)) return GameResult.gameIsEnd(null);

        GameResult resultCheck = checkForCurrentPlayersOfThisGame(currentGame, playerId);
        if (resultCheck != null) return resultCheck;

        if (isMoveInvalid(currentGame, row, col)) {
            saveWithStatus(currentGame, GameStatus.INVALID_MOVE);
            return GameResult.invalidMove(currentGame, "Некорректный ход");
        }
        int valuePlayer = currentGame.getFirstPlayer() == Constant.PLAYER ? Constant.PLAYER_X : Constant.PLAYER_O;
        int valueComp = currentGame.getFirstPlayer() == Constant.COMPUTER ? Constant.PLAYER_X : Constant.PLAYER_O;

        currentGame.getGameField().setValue(row, col, valuePlayer);
        GameResult resultPlayer = checkWinOrDraw(currentGame, valuePlayer, playerId, GameStatus.PLAYER_WIN);
        if (resultPlayer != null) return resultPlayer;

        makeComputerMove(currentGame, valueComp, valuePlayer);
        GameResult resultComp = checkWinOrDraw(currentGame, valueComp, null, GameStatus.COMPUTER_WIN);
        if (resultComp != null) return resultComp;

        saveWithStatus(currentGame, GameStatus.GAME_CONTINUES);
        return GameResult.continuesGame(currentGame);
    }

    @Override
    public GameResult makePlayerMove(ModelCurrentGame currentGame, int row, int col, UUID currentPlayer) {
        if (isGameOver(currentGame)) return GameResult.gameIsEnd(null);

        if (currentGame.getGameStatus().equals(GameStatus.WAITING_FOR_PLAYERS)) {
            return GameResult.waitingSecondPlayer(currentGame);
        }

        GameResult resultCheck = checkForCurrentPlayersOfThisGame(currentGame, currentPlayer);
        if (resultCheck !=null) return resultCheck;

        if (!currentPlayer.equals(currentGame.getCurrentPlayer())) {
            saveWithStatus(currentGame, GameStatus.INVALID_MOVE);
            return GameResult.invalidMove(currentGame, "Терпения.. сначала ходит твой противник!");
        }

        if (isMoveInvalid(currentGame, row, col)) {
            saveWithStatus(currentGame, GameStatus.INVALID_MOVE);
            return GameResult.invalidMove(currentGame, "Некорректный ход");
        }

        int value = currentPlayer.equals(currentGame.getIdPlayerX()) ? 1 : 2;
        currentGame.getGameField().setValue(row, col, value);

        GameResult result = checkWinOrDraw(currentGame, value, currentPlayer, null);
        if (result != null) return result;

        currentGame.setCurrentPlayer(switchPlayer(currentGame, currentPlayer));
        saveWithStatus(currentGame, GameStatus.GAME_CONTINUES);

        return currentPlayer.equals(currentGame.getIdPlayerX()) ?
                GameResult.continuesGame(currentGame, currentGame.getIdPlayerO()) :
                GameResult.continuesGame(currentGame, currentGame.getIdPlayerX());
    }

    @Override
    public void makeComputerMove(ModelCurrentGame currentGame, int valueComp, int valuePlayer) {
        int[][] field = currentGame.getGameField().getGameMatrix();
        if (!moveStrategy.isComputerTurn(field, currentGame.getFirstPlayer())) {
            int[] bestMove = moveStrategy.findBestCompMove(field, valueComp, valuePlayer);

            if (bestMove[0] != -1 && bestMove[1] != -1) {
                currentGame.getGameField().setValue(bestMove[0], bestMove[1], valueComp);
                saveWithStatus(currentGame, GameStatus.GAME_CONTINUES);
            }
        }
    }

    @Override
    public List<ModelCurrentGame> getAvailableGames() {
        return gameRepository.findByStatus(GameStatus.WAITING_FOR_PLAYERS);
    }

    @Override
    public List<ModelCurrentGame> getFinishedGames(UUID playerId) {
        return gameRepository.getFinishedGames(playerId);
    }

    @Override
    public GameResult joinTheGame(ModelCurrentGame currentGame, UUID playerId) {
        if (playerId.equals(currentGame.getIdPlayerX())) {
            return GameResult.invalidMove(null, "Нельзя присоединится в игру, которую сам создал, найти друга!");
        }
        if (!currentGame.getGameStatus().equals(GameStatus.WAITING_FOR_PLAYERS)) {
            return GameResult.invalidMove(null, "К игре нельзя присоединится! Найти игру со статусом WAITING_FOR_PLAYERS");
        }
        currentGame.setIdPlayerO(playerId);
        saveWithStatus(currentGame, GameStatus.GAME_CONTINUES);

        return GameResult.continuesGame(currentGame, currentGame.getIdPlayerX());
    }

    @Override
    public List<LeaderBoard> getTopPlayer(int limit) {
        return gameRepository.getTopPlayer(limit);
    }

    public boolean isMoveInvalid(ModelCurrentGame currentGame, int row, int col) {
        if (row < 0 || row >= Constant.ROW ||
                col < 0 || col >= Constant.COL) return true;
        return currentGame.getGameField().getValue(row, col) != Constant.EMPTY;
    }

    private void makeComputerFirstStep(ModelCurrentGame currentGame) {
        int row = random.nextInt(Constant.ROW);
        int col = random.nextInt(Constant.COL);
        currentGame.getGameField().setValue(row, col, Constant.PLAYER_X);
    }

    private UUID switchPlayer(ModelCurrentGame currentGame, UUID currentPlayer) {
        return currentGame.getIdPlayerX().equals(currentPlayer) ?
                currentGame.getIdPlayerO() :
                currentGame.getIdPlayerX();
    }

    private boolean isGameOver(ModelCurrentGame currentGame) {
        return !currentGame.getGameStatus().equals(GameStatus.GAME_CONTINUES) &&
                !currentGame.getGameStatus().equals(GameStatus.INVALID_MOVE) &&
                !currentGame.getGameStatus().equals(GameStatus.WAITING_FOR_PLAYERS);
    }

    private void saveWithStatus(ModelCurrentGame game, GameStatus status) {
        game.setGameStatus(status);
        gameRepository.save(game);
    }

    private GameResult checkWinOrDraw(ModelCurrentGame game, int value, UUID playerId, GameStatus status) {
        if (game.getOpponent().equals(Constant.COMP)) {
            if (winChecker.isWinGame(game.getGameField().getGameMatrix(), value)) {
                saveWithStatus(game, status);
                return status.equals(GameStatus.COMPUTER_WIN) ?
                        GameResult.computerWins(game) :
                        GameResult.playerWins(game);
            }
        } else {
            if (winChecker.isWinGame(game.getGameField().getGameMatrix(), value)) {
                saveWithStatus(game, value == Constant.PLAYER_X ? GameStatus.PLAYER_X_WIN : GameStatus.PLAYER_O_WIN);
                return playerId.equals(game.getIdPlayerX()) ?
                        GameResult.playerWins(game, game.getIdPlayerX()) :
                        GameResult.playerWins(game, game.getIdPlayerO());
            }
        }
        if (winChecker.noFreeCells(game.getGameField().getGameMatrix())) {
            saveWithStatus(game, GameStatus.ZERO_WIN);
            return GameResult.zeroWins(game);
        }
        return null;
    }

    private GameResult checkForCurrentPlayersOfThisGame(ModelCurrentGame currentGame, UUID playerId) {
        boolean isPlayerX = currentGame.getIdPlayerX() != null && currentGame.getIdPlayerX().equals(playerId);
        boolean isPlayerO = currentGame.getIdPlayerO() != null && currentGame.getIdPlayerO().equals(playerId);

        if (!isPlayerX && !isPlayerO) {
            saveWithStatus(currentGame, GameStatus.INVALID_MOVE);
            return currentGame.getOpponent().equals(Constant.COMP) ?
                    GameResult.invalidMove(null,
                            "Это не твоя игра! Это игра для пользователя, который создал эту парию игры.") :
                    GameResult.invalidMove(null,
                            "В этой игре слишком тесно для третьего игрока! Найди себе другую игру!");
        }
        return null;
    }
}
