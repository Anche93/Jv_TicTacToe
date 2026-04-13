package org.example.domain.service;

import org.example.domain.model.GameResult;
import org.example.domain.model.ModelCurrentGame;

import java.util.UUID;

public interface GameService {

    int determineFirstPlayer(ModelCurrentGame currentGame);

    GameResult processMove(UUID gameId, int row, int col, UUID playerId);

    GameResult makePlayerMove(UUID gameId, int row, int col, UUID currentPlayer);

    ModelCurrentGame makeComputerMove(UUID gameId, int valueComp);

    GameResult joinTheGame(UUID gameId, UUID playerId);
}
