package org.example.domain.service;

import org.example.domain.model.GameResult;
import org.example.domain.model.LeaderBoard;
import org.example.domain.model.ModelCurrentGame;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameService {

    ModelCurrentGame createGameWithComp(UUID playerId);

    ModelCurrentGame createGameWithFriend(UUID playerId);

    Optional<ModelCurrentGame> getGameById(UUID gameId);

    int determineFirstPlayer(ModelCurrentGame currentGame);

    GameResult processMove(ModelCurrentGame currentGame, int row, int col, UUID playerId);

    GameResult makePlayerMove(ModelCurrentGame currentGame, int row, int col, UUID currentPlayer);

    void makeComputerMove(ModelCurrentGame currentGame, int valueComp, int valuePlayer);

    List<ModelCurrentGame> getAvailableGames();

    List<ModelCurrentGame> getFinishedGames(UUID playerId);

    GameResult joinTheGame(ModelCurrentGame currentGame, UUID playerId);

    List<LeaderBoard> getTopPlayer(int limit);
}
