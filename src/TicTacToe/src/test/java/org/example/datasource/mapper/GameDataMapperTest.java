package org.example.datasource.mapper;

import org.example.datasource.model.GameEntity;
import org.example.domain.model.Constant;
import org.example.domain.model.GameStatus;
import org.example.domain.model.ModelCurrentGame;
import org.example.domain.model.ModelGameField;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameDataMapperTest {

    @Test
    void testToEntity_ShouldMapAllFields() {
        ModelCurrentGame domain = new ModelCurrentGame();
        UUID gameId = UUID.randomUUID();
        domain.setUuid(gameId);

        int[][] matrix = {{1, 0, 2}, {0, 1, 0}, {2, 0, 0}};
        ModelGameField field = new ModelGameField();
        field.setGameMatrix(matrix);
        domain.setGameField(field);

        UUID playerX = UUID.randomUUID();
        domain.setIdPlayerX(playerX);
        domain.setOpponent(Constant.COMP);
        domain.setGameStatus(GameStatus.GAME_CONTINUES);
        domain.setFirstPlayer(Constant.PLAYER);
        domain.setCreatedAt(LocalDateTime.now());

        GameEntity entity = GameDataMapper.toEntity(domain);

        assertEquals(domain.getUuid(), entity.getGameUuid());
        assertArrayEquals(domain.getGameField().getGameMatrix(), entity.getField());
        assertEquals(domain.getIdPlayerX(), entity.getIdPlayerX());
        assertNull(entity.getIdPlayerO());
        assertEquals(domain.getOpponent(), entity.getOpponent());
        assertEquals(domain.getGameStatus(), entity.getGameStatus());
        assertEquals(domain.getFirstPlayer(), entity.getFirstPlayer());
        assertNull(entity.getCurrentPlayer());
        assertEquals(domain.getCreatedAt(), entity.getCreatedAt());
    }

    @Test
    void testToEntity_ShouldHandleNullValue() {
        ModelCurrentGame domain = new ModelCurrentGame();
        GameEntity entity = GameDataMapper.toEntity(domain);

        assertNotNull(entity.getGameUuid());
        assertNotNull(entity.getField());
        assertNull(entity.getIdPlayerX());
        assertNull(entity.getIdPlayerO());
        assertNull(entity.getOpponent());
        assertNull(entity.getGameStatus());
        assertEquals(0, entity.getFirstPlayer());
        assertNull(entity.getCurrentPlayer());
    }

    @Test
    void testToDomain_ShouldMapAllFields() {
        int[][] matrix = {{1, 0, 2}, {0, 1, 0}, {2, 0, 0}};
        UUID playerX = UUID.randomUUID();

        GameEntity entity = new GameEntity(
                UUID.randomUUID(), matrix,
                playerX, UUID.randomUUID(), Constant.FRIEND, GameStatus.GAME_CONTINUES,
                Constant.PLAYER, playerX, LocalDateTime.now()
        );

        ModelCurrentGame domain = GameDataMapper.toDomain(entity);

        assertEquals(entity.getGameUuid(), domain.getUuid());
        assertArrayEquals(entity.getField(), domain.getGameField().getGameMatrix());
        assertEquals(entity.getIdPlayerX(), domain.getIdPlayerX());
        assertEquals(entity.getIdPlayerO(), domain.getIdPlayerO());
        assertEquals(entity.getOpponent(), domain.getOpponent());
        assertEquals(entity.getGameStatus(), domain.getGameStatus());
        assertEquals(entity.getFirstPlayer(), domain.getFirstPlayer());
        assertEquals(entity.getCurrentPlayer(), domain.getCurrentPlayer());
        assertEquals(entity.getCreatedAt(), domain.getCreatedAt());
    }

    @Test
    void testToDomain_ShouldHandleNullEntity() {
        GameEntity entity = new GameEntity();
        entity.setGameUuid(null);
        entity.setField(null);

        ModelCurrentGame domain = GameDataMapper.toDomain(entity);

        assertNotNull(domain);
        assertNotNull(domain.getGameField());
    }
}
