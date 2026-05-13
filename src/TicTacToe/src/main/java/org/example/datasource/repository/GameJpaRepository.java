package org.example.datasource.repository;

import org.example.datasource.model.GameEntity;
import org.example.domain.model.GameStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameJpaRepository extends CrudRepository<GameEntity, UUID> {

    Iterable<GameEntity> findByGameStatus(GameStatus status);

    @Query("SELECT g FROM GameEntity g " +
            "WHERE (g.idPlayerX = :uuid OR g.idPlayerO = :uuid) " +
            "AND g.gameStatus IN ('PLAYER_WIN', 'PLAYER_X_WIN', " +
            "'PLAYER_O_WIN', 'ZERO_WIN')")
    List<GameEntity> findAllFinishGamesByUserUuid(@Param("uuid") UUID uuid);

//    @Query(value = """
//            SELECT
//                u.user_id AS userId, u.user_login AS userLogin,
//                COALESCE(
//                    CAST(SUM(
//                        CASE
//                            WHEN (g.Status IN ('PLAYER_WIN', 'PLAYER_X_WIN') AND g.Player_X = u.user_id) THEN 1
//                            WHEN (g.Status IN ('PLAYER_WIN', 'PLAYER_O_WIN') AND g.Player_O = u.user_id) THEN 1
//                            ELSE 0
//                        END
//                    ) AS FLOAT) /
//                    NULLIF(SUM(
//                        CASE
//                            WHEN (g.Status IN ('COMPUTER_WIN', 'PLAYER_X_WIN') AND g.Player_O = u.user_id) THEN 1
//                            WHEN (g.Status IN ('COMPUTER_WIN', 'PLAYER_O_WIN') AND g.Player_X = u.user_id) THEN 1
//                            WHEN g.Status = 'ZERO_WIN' THEN 1
//                            ELSE 0
//                        END
//                    ), 0) * 100,
//                0) AS winPercent
//            FROM users u
//            LEFT JOIN games g ON (u.user_id = g.Player_X OR u.user_id = g.Player_O)
//            GROUP BY u.user_id, u.user_login
//            HAVING SUM(
//                CASE
//                    WHEN (g.Status IN ('COMPUTER_WIN', 'PLAYER_X_WIN') AND g.Player_O = u.user_id) THEN 1
//                    WHEN (g.Status IN ('COMPUTER_WIN', 'PLAYER_O_WIN') AND g.Player_X = u.user_id) THEN 1
//                    WHEN g.Status = 'ZERO_WIN' THEN 1
//                    ELSE 0
//                END
//            ) > 0
//            ORDER BY winPercent DESC
//            LIMIT :limit
//            """, nativeQuery = true)
//    List<Object[]> findTopPlayersNative(@Param("limit") int limit);

    @Query(value = """
            SELECT
                u.user_id AS userId, u.user_login AS userLogin,
                COALESCE(
                    CAST(SUM(
                        CASE
                            WHEN (g.Status IN ('PLAYER_WIN', 'PLAYER_X_WIN') AND g.Player_X = u.user_id) THEN 1
                            WHEN (g.Status IN ('PLAYER_WIN', 'PLAYER_O_WIN') AND g.Player_O = u.user_id) THEN 1
                            ELSE 0
                        END
                    ) AS FLOAT) /
                    NULLIF(SUM(
                        CASE
                            WHEN g.Status IN ('PLAYER_WIN', 'PLAYER_X_WIN', 'PLAYER_O_WIN', 'ZERO_WIN', 'COMPUTER_WIN') THEN 1
                            ELSE 0
                        END
                    ), 0) * 100,
                0) AS winPercent
            FROM users u
            LEFT JOIN games g ON (u.user_id = g.Player_X OR u.user_id = g.Player_O)
            GROUP BY u.user_id, u.user_login
            ORDER BY winPercent DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findTopPlayersNative(@Param("limit") int limit);
}
