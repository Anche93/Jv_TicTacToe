package org.example.datasource.model;

import org.example.datasource.mapper.GameDataMapper;
import org.example.domain.model.ModelCurrentGame;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GameStorage {
    private final ConcurrentMap<UUID, GameEntity> gameStorage;

    public GameStorage() {
        gameStorage = new ConcurrentHashMap<>();
    }

    public void addGame(ModelCurrentGame game) {
        GameEntity entity = GameDataMapper.toEntity(game);
        gameStorage.put(game.getUuid(), entity);
    }

    public ModelCurrentGame getCurrentGame(UUID uuid) {
        GameEntity entity = gameStorage.get(uuid);
        if (entity == null) return null;

        return GameDataMapper.toDomain(entity);
    }
}
