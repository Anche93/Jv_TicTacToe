package org.example.datasource.mapper;

import org.example.datasource.model.GameEntity;
import org.example.domain.model.ModelCurrentGame;
import org.example.domain.model.ModelGameField;

public class GameDataMapper {

    public static GameEntity toEntity(ModelCurrentGame currentGame) {
        return new GameEntity(
                currentGame.getUuid(),
                currentGame.getGameField().getGameMatrix(),
                currentGame.getIdPlayerX(),
                currentGame.getIdPlayerO(),
                currentGame.getOpponent(),
                currentGame.getGameStatus(),
                currentGame.getFirstPlayer(),
                currentGame.getCurrentPlayer());
    }

    public static ModelCurrentGame toDomain(GameEntity entity) {
        ModelGameField field = new ModelGameField();
        field.setGameMatrix(entity.getField());

        ModelCurrentGame game = new ModelCurrentGame(entity.getGameUuid(), field);
        game.setIdPlayerX(entity.getIdPlayerX());
        game.setIdPlayerO(entity.getIdPlayerO());
        game.setOpponent(entity.getOpponent());
        game.setGameStatus(entity.getGameStatus());
        game.setFirstPlayer(entity.getFirstPlayer());
        game.setCurrentPlayer(entity.getCurrentPlayer());
        return game;
    }
}
