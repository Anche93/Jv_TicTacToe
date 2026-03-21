package org.example.datasource.repository;

import org.example.datasource.model.GameStorage;
import org.example.domain.model.ModelCurrentGame;

import java.util.UUID;

public class GameRepository {
    private final GameStorage gameStorage;

    public GameRepository(GameStorage gameStorage) {
        this.gameStorage = gameStorage;
    }

    public void saveGame(ModelCurrentGame currentGame) {
        gameStorage.addGame(currentGame);
    }

    public ModelCurrentGame getCurrentGame(UUID uuid) {
        return gameStorage.getCurrentGame(uuid);
    }
}
