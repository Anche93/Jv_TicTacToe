package org.example.web.mapper;

import org.example.datasource.model.GameEntity;
import org.example.domain.model.ModelCurrentGame;
import org.example.web.model.CurrentGameDto;
import org.example.web.model.GameFieldDto;

public class GameWebMapper {
    public static CurrentGameDto toDto(ModelCurrentGame currentGame) {
        if (currentGame == null) return null;

        GameFieldDto fieldDto = new GameFieldDto(currentGame.getGameField().getGameMatrix());

        return new CurrentGameDto(
                currentGame.getUuid(),
                fieldDto,
                currentGame.getIdPlayerX(),
                currentGame.getIdPlayerO(),
                currentGame.getGameStatus(),
                currentGame.getCurrentPlayer(),
                currentGame.getOpponent(),
                currentGame.getFirstPlayer());
    }

    public static CurrentGameDto toDto(GameEntity gameEntity) {
        if (gameEntity == null) return null;

        GameFieldDto fieldDto = new GameFieldDto(gameEntity.getField());

        return new CurrentGameDto(
                gameEntity.getGameUuid(),
                fieldDto,
                gameEntity.getIdPlayerX(),
                gameEntity.getIdPlayerO(),
                gameEntity.getGameStatus(),
                gameEntity.getCurrentPlayer(),
                gameEntity.getOpponent(),
                gameEntity.getFirstPlayer());
    }
}
