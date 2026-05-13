package org.example.datasource.repository;

import org.example.datasource.mapper.GameDataMapper;
import org.example.datasource.model.GameEntity;
import org.example.domain.model.GameStatus;
import org.example.domain.model.LeaderBoard;
import org.example.domain.model.ModelCurrentGame;
import org.example.domain.port.GameRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class GameRepositoryAdapter implements GameRepositoryPort {

    private final GameJpaRepository jpaRepository;

    public GameRepositoryAdapter(GameJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<ModelCurrentGame> findById(UUID id) {
        return jpaRepository.findById(id).map(GameDataMapper::toDomain);
    }

    @Override
    public ModelCurrentGame save(ModelCurrentGame game) {
        GameEntity entity = GameDataMapper.toEntity(game);
        GameEntity saved = jpaRepository.save(entity);
        return GameDataMapper.toDomain(saved);
    }

    @Override
    public List<ModelCurrentGame> findByStatus(GameStatus status) {
        List<ModelCurrentGame> list = new ArrayList<>();
        jpaRepository.findByGameStatus(status)
                .forEach(gameEntity -> list.add(GameDataMapper.toDomain(gameEntity)));
        return list;
    }

    @Override
    public List<ModelCurrentGame> getFinishedGames(UUID playerId) {
        List<GameEntity> finishedGames = jpaRepository.findAllFinishGamesByUserUuid(playerId);
        return finishedGames
                .stream()
                .map(GameDataMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaderBoard> getTopPlayer(int limit) {
        List<Object[]> list = jpaRepository.findTopPlayersNative(limit);
        return list.stream()
                .map(row -> new LeaderBoard(
                        (UUID) row[0],
                        (String) row[1],
                        (Double) row[2]
                )).collect(Collectors.toList());
    }
}
