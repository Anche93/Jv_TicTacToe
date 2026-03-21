package org.example.domain.service;

import org.example.domain.model.GameResult;
import org.example.domain.model.ModelCurrentGame;

import java.util.UUID;

public interface GameService {

    int determineFirstPlayer(ModelCurrentGame currentGame);

    GameResult processMove(UUID gameId, int row, int col);

    ModelCurrentGame makeComputerMove(UUID gameId);

    boolean validatePlayerMove(UUID gameId, int row, int col);

    boolean isWinGame(int[][] gameField, int winner);

    boolean isGameOver(int[][] gameField);

    boolean isPlayerMove(UUID gameId);
}
