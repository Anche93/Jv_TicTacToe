package org.example.datasource.repository;

import org.example.datasource.model.GameEntity;
import org.example.domain.model.GameStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GameRepository extends CrudRepository<GameEntity, UUID> {

    Iterable<GameEntity> findByGameStatus(GameStatus status);
}
