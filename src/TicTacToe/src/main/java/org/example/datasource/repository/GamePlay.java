package org.example.datasource.repository;

import org.example.domain.model.Constant;
import org.example.domain.model.GameResult;
import org.example.domain.model.ModelCurrentGame;
import org.example.domain.service.GameService;

import java.util.Random;
import java.util.UUID;

public class GamePlay implements GameService {
    private final GameRepository gameRepository;
    private final Random random = new Random();

    public GamePlay(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public int determineFirstPlayer(ModelCurrentGame currentGame) {
        int firstPlayer = random.nextInt(2) + 1;
        if (firstPlayer == Constant.COMPUTER) {
            makeComputerFirstStep(currentGame);
            gameRepository.saveGame(currentGame);
        }
        return firstPlayer;
    }

    @Override
    public GameResult processMove(UUID gameId, int row, int col) {
        ModelCurrentGame currentGame = gameRepository.getCurrentGame(gameId);

        if (!validatePlayerMove(gameId, row, col)) {
            return GameResult.invalidMove(currentGame, "Некорректный ход");
        }
        currentGame.getGameField().setValue(row, col, Constant.PLAYER);
        if (isWinGame(currentGame.getGameField().getGameMatrix(), Constant.PLAYER)) {
            gameRepository.saveGame(currentGame);
            return GameResult.playerWins(currentGame);
        }

        currentGame = makeComputerMove(gameId);
        if (isWinGame(currentGame.getGameField().getGameMatrix(), Constant.COMPUTER)) {
            gameRepository.saveGame(currentGame);
            return GameResult.computerWins(currentGame);
        }

        if (isGameOver(currentGame.getGameField().getGameMatrix())) {
            gameRepository.saveGame(currentGame);
            return GameResult.zeroWins(currentGame);
        }

        gameRepository.saveGame(currentGame);
        return GameResult.continuesGame(currentGame);
    }

    @Override
    public ModelCurrentGame makeComputerMove(UUID gameId) {
        ModelCurrentGame currentGame = gameRepository.getCurrentGame(gameId);

        if (!isPlayerMove(gameId)) {
            int[] bestMove = findBestCompMove(currentGame.getGameField().getGameMatrix());

            if (bestMove[0] != -1 && bestMove[1] != -1) {
                currentGame.getGameField().setValue(bestMove[0], bestMove[1], Constant.COMPUTER);
                gameRepository.saveGame(currentGame);
            }
        }
        return currentGame;
    }

    @Override
    public boolean validatePlayerMove(UUID gameId, int row, int col) {
        ModelCurrentGame currentGame = gameRepository.getCurrentGame(gameId);

        if (currentGame == null) return false;
        if (!isPlayerMove(gameId)) return false;

        if (row < 0 || row >= Constant.ROW ||
                col < 0 || col >= Constant.COL) return false;

        return currentGame.getGameField().getValue(row, col) == Constant.EMPTY;
    }

    @Override
    public boolean isWinGame(int[][] gameField, int winner) {
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

    @Override
    public boolean isGameOver(int[][] gameField) {
        if (isWinGame(gameField, Constant.PLAYER) ||
                isWinGame(gameField, Constant.COMPUTER)) {
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

    @Override
    public boolean isPlayerMove(UUID gameId) {
        ModelCurrentGame currentGame = gameRepository.getCurrentGame(gameId);
        int[][] field = currentGame.getGameField().getGameMatrix();

        int countX = 0;
        int countO = 0;

        for (int i = 0; i < Constant.ROW; i++) {
            for (int j = 0; j < Constant.COL; j++) {
                if (field[i][j] == Constant.PLAYER) countX++;
                if (field[i][j] == Constant.COMPUTER) countO++;
            }
        }
        if (countX == 0 && countO == 0) {
            return currentGame.getFirstPlayer() == Constant.PLAYER;
        }

        if (countX == countO) {
            return currentGame.getFirstPlayer() == Constant.PLAYER;
        }
        if (countX > countO) {
            return currentGame.getFirstPlayer() == Constant.COMPUTER;
        }
        return true;
    }

    private void makeComputerFirstStep(ModelCurrentGame currentGame) {
        int row = random.nextInt(Constant.ROW);
        int col = random.nextInt(Constant.COL);
        currentGame.getGameField().setValue(row, col, Constant.COMPUTER);
    }

    private int minimax(int[][] field, int depth, boolean compMove) {
        int score = evaluateScore(field);
        if (score == 10 || score == -10) return score;

        if (isGameOver(field)) return 0;

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
}
