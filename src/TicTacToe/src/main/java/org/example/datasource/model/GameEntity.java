package org.example.datasource.model;

import java.util.UUID;

public class GameEntity {
    private UUID uuid;
    private int[][] field;
    private int firstPlayer;

    public GameEntity() {
    }

    public GameEntity(UUID uuid, int[][] field, int firstPlayer) {
        this.uuid = uuid;
        this.field = field;
        this.firstPlayer = firstPlayer;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int[][] getField() {
        return field;
    }

    public void setField(int[][] field) {
        this.field = field;
    }

    public int getFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(int firstPlayer) {
        this.firstPlayer = firstPlayer;
    }
}
