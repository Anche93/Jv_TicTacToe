package org.example.datasource.mapper;

import org.example.datasource.model.GameEntity;
import org.example.domain.model.ModelCurrentGame;
import org.example.domain.model.ModelGameField;

public class GameDataMapper {

    public static GameEntity toEntity(ModelCurrentGame currentGame) {
        return new GameEntity(
                currentGame.getUuid(),
                currentGame.getGameField().getGameMatrix(),
                currentGame.getFirstPlayer());
    }

    public static ModelCurrentGame toDomain(GameEntity entity) {
        ModelGameField field = new ModelGameField();
        field.setGameMatrix(entity.getField());

        ModelCurrentGame game = new ModelCurrentGame(entity.getUuid(), field);
        game.setFirstPlayer(entity.getFirstPlayer());
        return game;
    }
}
