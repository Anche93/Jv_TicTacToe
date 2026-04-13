package org.example.datasource.repository;

import org.example.datasource.mapper.GameDataMapper;
import org.example.datasource.model.GameEntity;
import org.example.domain.model.*;
import org.example.domain.service.GameService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class GameServiceImp implements GameService {
    private final GameRepository gameRepository;
    private final Random random = new Random();

    public GameServiceImp(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
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
    public GameResult processMove(UUID gameId, int row, int col, UUID playerId) {
        Optional<GameEntity> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isEmpty()) return GameResult.emptyGame(null);
        if (isGameOver(gameId)) return GameResult.gameIsEnd(null);

        ModelCurrentGame currentGame = GameDataMapper.toDomain(optionalGame.get());

        boolean isPlayerX = currentGame.getIdPlayerX() != null && currentGame.getIdPlayerX().equals(playerId);
        boolean isPlayerO = currentGame.getIdPlayerO() != null && currentGame.getIdPlayerO().equals(playerId);

        if (!isPlayerX && !isPlayerO) {
            return GameResult.invalidMove(null,
                    "Это не твоя игра! Это игра для пользователя, который создал эту парию игры.");
        }

        if (!validatePlayerMove(gameId, row, col)) {
            currentGame.setGameStatus(GameStatus.INVALID_MOVE);
            gameRepository.save(GameDataMapper.toEntity(currentGame));
            return GameResult.invalidMove(currentGame, "Некорректный ход");
        }
        int valuePlayer = currentGame.getFirstPlayer() == Constant.PLAYER ? Constant.PLAYER_X : Constant.PLAYER_O;
        int valueComp = currentGame.getFirstPlayer() == Constant.COMPUTER ? Constant.PLAYER_X : Constant.PLAYER_O;

        currentGame.getGameField().setValue(row, col, valuePlayer);
        if (isWinGame(currentGame.getGameField().getGameMatrix(), valuePlayer)) {
            currentGame.setGameStatus(GameStatus.PLAYER_WIN);
            gameRepository.save(GameDataMapper.toEntity(currentGame));
            return GameResult.playerWins(currentGame);
        }

        currentGame = makeComputerMove(gameId, valueComp);
        if (isWinGame(currentGame.getGameField().getGameMatrix(), valueComp)) {
            currentGame.setGameStatus(GameStatus.COMPUTER_WIN);
            gameRepository.save(GameDataMapper.toEntity(currentGame));
            return GameResult.computerWins(currentGame);
        }

        if (noFreeCells(currentGame.getGameField().getGameMatrix())) {
            currentGame.setGameStatus(GameStatus.ZERO_WIN);
            gameRepository.save(GameDataMapper.toEntity(currentGame));
            return GameResult.zeroWins(currentGame);
        }

        currentGame.setGameStatus(GameStatus.GAME_CONTINUES);
        gameRepository.save(GameDataMapper.toEntity(currentGame));

        return GameResult.continuesGame(currentGame);
    }

    @Override
    public GameResult makePlayerMove(UUID gameId, int row, int col, UUID currentPlayer) {
        Optional<GameEntity> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isEmpty()) return GameResult.emptyGame(null);
        if (isGameOver(gameId)) return GameResult.gameIsEnd(null);

        ModelCurrentGame currentGame = GameDataMapper.toDomain(optionalGame.get());

        if (!currentPlayer.equals(currentGame.getCurrentPlayer())) {
            currentGame.setGameStatus(GameStatus.INVALID_MOVE);
            gameRepository.save(GameDataMapper.toEntity(currentGame));
            return GameResult.invalidMove(currentGame, "Терпения.. сначала ходит твой противник!");
        }

        if (!validatePlayerMove(gameId, row, col)) {
            currentGame.setGameStatus(GameStatus.INVALID_MOVE);
            gameRepository.save(GameDataMapper.toEntity(currentGame));
            return GameResult.invalidMove(currentGame, "Некорректный ход");
        }

        int value = currentPlayer.equals(currentGame.getIdPlayerX()) ? 1 : 2;
        currentGame.getGameField().setValue(row, col, value);

        if (isWinGame(currentGame.getGameField().getGameMatrix(), value)) {
           currentGame.setGameStatus(value == Constant.PLAYER_X ? GameStatus.PLAYER_X_WIN : GameStatus.PLAYER_O_WIN);
            gameRepository.save(GameDataMapper.toEntity(currentGame));
            return currentPlayer.equals(currentGame.getIdPlayerX()) ?
                    GameResult.playerWins(currentGame, currentGame.getIdPlayerX()) :
                    GameResult.playerWins(currentGame, currentGame.getIdPlayerO());
        }

        if (noFreeCells(currentGame.getGameField().getGameMatrix())) {
            currentGame.setGameStatus(GameStatus.ZERO_WIN);
            gameRepository.save(GameDataMapper.toEntity(currentGame));
            return GameResult.zeroWins(currentGame);
        }

        currentGame.setGameStatus(GameStatus.GAME_CONTINUES);
        currentGame.setCurrentPlayer(switchPlayer(gameId, currentPlayer));
        gameRepository.save(GameDataMapper.toEntity(currentGame));

        return currentPlayer.equals(currentGame.getIdPlayerX()) ?
                GameResult.continuesGame(currentGame, currentGame.getIdPlayerO()) :
                GameResult.continuesGame(currentGame, currentGame.getIdPlayerX());
    }

    @Override
    public ModelCurrentGame makeComputerMove(UUID gameId, int valueComp) {
        Optional<GameEntity> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isEmpty()) return null;

        ModelCurrentGame currentGame = GameDataMapper.toDomain(optionalGame.get());

        if (!isPlayerMove(gameId)) {
            int[] bestMove = findBestCompMove(currentGame.getGameField().getGameMatrix());

            if (bestMove[0] != -1 && bestMove[1] != -1) {
                currentGame.getGameField().setValue(bestMove[0], bestMove[1], valueComp);
                currentGame.setGameStatus(GameStatus.GAME_CONTINUES);
                gameRepository.save(GameDataMapper.toEntity(currentGame));
            }
        }
        return currentGame;
    }

    @Override
    public GameResult joinTheGame(UUID gameId, UUID playerId) {
        Optional<GameEntity> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isEmpty()) return GameResult.emptyGame(null);

        ModelCurrentGame currentGame = GameDataMapper.toDomain(optionalGame.get());
        if (playerId.equals(currentGame.getIdPlayerX())) {
            return GameResult.invalidMove(null, "Нельзя присоединится в игру, которую сам создал, найти друга!");
        }
        if (!currentGame.getGameStatus().equals(GameStatus.WAITING_FOR_PLAYERS)) {
            return GameResult.invalidMove(null, "К игре нельзя присоединится! Найти игру со статусом WAITING_FOR_PLAYERS");
        }
        currentGame.setIdPlayerO(playerId);
        currentGame.setGameStatus(GameStatus.GAME_CONTINUES);
        gameRepository.save(GameDataMapper.toEntity(currentGame));

        return GameResult.continuesGame(currentGame, currentGame.getIdPlayerX());
    }

    private boolean validatePlayerMove(UUID gameId, int row, int col) {
        Optional<GameEntity> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isEmpty()) return false;

        ModelCurrentGame currentGame = GameDataMapper.toDomain(optionalGame.get());
        if (row < 0 || row >= Constant.ROW ||
                col < 0 || col >= Constant.COL) return false;

        return currentGame.getGameField().getValue(row, col) == Constant.EMPTY;
    }

    private boolean isWinGame(int[][] gameField, int winner) {
        int countHorizontal = 0;
        int countVertical = 0;

        for (int i = 0; i < Constant.ROW; i++) {
            countHorizontal = 0;
            countVertical = 0;

            for (int j = 0; j < Constant.COL; j++) {
                if (gameField[i][j] == winner) countHorizontal++;
                if (gameField[j][i] == winner) countVertical++;
            }
            if (countVertical == Constant.COL || countHorizontal == Constant.ROW) {
                return true;
            }

        }
        int countDiagonalLeft = 0;
        int countDiagonalRight = 0;

        for (int i = 0; i < Constant.ROW; i++) {
            if (gameField[i][i] == winner) countDiagonalLeft++;
            if (gameField[i][Constant.ROW - 1 - i] == winner) countDiagonalRight++;
        }
        return countDiagonalLeft == Constant.ROW || countDiagonalRight == Constant.ROW;
    }

    private boolean noFreeCells(int[][] gameField) {
        if (isWinGame(gameField, Constant.PLAYER_X) ||
                isWinGame(gameField, Constant.PLAYER_O)) {
            return true;
        }
        for (int i = 0; i < Constant.ROW; i++) {
            for (int j = 0; j < Constant.COL; j++) {
                if (gameField[i][j] == Constant.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isPlayerMove(UUID gameId) {
        Optional<GameEntity> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isEmpty()) return false;

        ModelCurrentGame currentGame = GameDataMapper.toDomain(optionalGame.get());
        int[][] field = currentGame.getGameField().getGameMatrix();

        int countX = 0;
        int countO = 0;

        for (int i = 0; i < Constant.ROW; i++) {
            for (int j = 0; j < Constant.COL; j++) {
                if (field[i][j] == Constant.PLAYER_X) countX++;
                if (field[i][j] == Constant.PLAYER_O) countO++;
            }
        }
        if (countX == 0 && countO == 0) {
            return currentGame.getFirstPlayer() == Constant.PLAYER_X;
        }

        if (countX == countO) {
            return currentGame.getFirstPlayer() == Constant.PLAYER_X;
        }
        if (countX > countO) {
            return currentGame.getFirstPlayer() == Constant.PLAYER_O;
        }
        return true;
    }

    private void makeComputerFirstStep(ModelCurrentGame currentGame) {
        int row = random.nextInt(Constant.ROW);
        int col = random.nextInt(Constant.COL);
        currentGame.getGameField().setValue(row, col, Constant.PLAYER_X);
    }

    private int minimax(int[][] field, int depth, boolean compMove) {
        int score = evaluateScore(field);
        if (score == 10 || score == -10) return score;

        if (noFreeCells(field)) return 0;

        int bestScore;
        if (compMove) {
            bestScore = -1000;
            for (int i = 0; i < Constant.ROW; i++) {
                for (int j = 0;  j < Constant.COL; j++) {
                    if (field[i][j] == Constant.EMPTY) {
                        field[i][j] = Constant.COMPUTER;
                        bestScore = Math.max(bestScore, minimax(field, depth + 1, false));
                        field[i][j] = 0;
                    }
                }
            }
        } else {
            bestScore = 1000;
            for (int i = 0; i < Constant.ROW; i++) {
                for (int j = 0; j < Constant.COL; j++) {
                    if (field[i][j] == Constant.EMPTY) {
                        field[i][j] = Constant.PLAYER;
                        bestScore = Math.min(bestScore, minimax(field, depth + 1, true));
                        field[i][j] = 0;
                    }
                }
            }
        }
        return bestScore;
    }

    private int evaluateScore(int[][] field) {
        if (isWinGame(field, Constant.COMPUTER)) return 10;
        if (isWinGame(field, Constant.PLAYER)) return -10;
        return 0;
    }

    private int[] findBestCompMove(int[][] field) {
        int bestScore = -1000;
        int[] bestCompMove = {-1, -1};

        for (int i = 0; i < Constant.ROW; i++) {
            for (int j = 0; j < Constant.COL; j++) {
                if (field[i][j] == Constant.EMPTY) {
                    field[i][j] = Constant.COMPUTER;

                    int movePrice = minimax(field, 0, false);
                    field[i][j] = Constant.EMPTY;

                    if (movePrice > bestScore) {
                        bestCompMove[0] = i;
                        bestCompMove[1] = j;
                        bestScore = movePrice;
                    }
                }
            }
        }
        return bestCompMove;
    }

    private UUID switchPlayer(UUID gameId, UUID currentPlayer) {
        Optional<GameEntity> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isEmpty()) return null;

        ModelCurrentGame currentGame = GameDataMapper.toDomain(optionalGame.get());
        return currentGame.getIdPlayerX().equals(currentPlayer) ?
                currentGame.getIdPlayerO() :
                currentGame.getIdPlayerX();
    }

    private boolean isGameOver(UUID gameId) {
        Optional<GameEntity> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isEmpty()) return true;

        ModelCurrentGame currentGame = GameDataMapper.toDomain(optionalGame.get());

        return !currentGame.getGameStatus().equals(GameStatus.GAME_CONTINUES) &&
                !currentGame.getGameStatus().equals(GameStatus.INVALID_MOVE) &&
                !currentGame.getGameStatus().equals(GameStatus.WAITING_FOR_PLAYERS);
    }
}
