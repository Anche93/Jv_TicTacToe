package org.example.domain.model;

public enum GameStatus {

    PLAYER_WIN,
    COMPUTER_WIN,
    PLAYER_X_WIN,
    PLAYER_O_WIN,
    ZERO_WIN,
    GAME_IS_END,

    WAITING_FOR_PLAYERS,
    GAME_CONTINUES,
    INVALID_MOVE,

    EMPTY_GAME;
}
