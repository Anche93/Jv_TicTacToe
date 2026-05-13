package org.example.domain.port;

import org.example.domain.model.GameStatus;
import org.example.domain.model.LeaderBoard;
import org.example.domain.model.ModelCurrentGame;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameRepositoryPort {

    Optional<ModelCurrentGame> findById(UUID id);

    ModelCurrentGame save(ModelCurrentGame game);

    List<ModelCurrentGame> findByStatus(GameStatus status);

    List<ModelCurrentGame> getFinishedGames(UUID id);

    List<LeaderBoard> getTopPlayer(int limit);
}
