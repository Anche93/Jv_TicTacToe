package org.example.web.mapper;

import org.example.domain.model.ModelCurrentGame;
import org.example.web.model.CurrentGameDto;
import org.example.web.model.GameFieldDto;

public class GameWebMapper {
    public static CurrentGameDto toDto(ModelCurrentGame currentGame) {
        if (currentGame == null) return null;

        GameFieldDto fieldDto = new GameFieldDto(currentGame.getGameField().getGameMatrix());

        return new CurrentGameDto(currentGame.getUuid(), fieldDto, currentGame.getFirstPlayer());
    }
}
