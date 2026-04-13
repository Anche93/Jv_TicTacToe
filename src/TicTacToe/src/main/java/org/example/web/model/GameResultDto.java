package org.example.web.model;

import org.example.domain.model.GameResult;
import org.example.domain.model.GameStatus;

public record GameResultDto(
        CurrentGameDto currentGameDto,
        GameStatus status,
        String massage) {

    public static GameResultDto fromDomain(GameResult result, CurrentGameDto currentGameDto) {
        return new GameResultDto(currentGameDto, result.getStatus(), result.getInfoMessage());
    }
}
