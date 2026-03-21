package org.example.di;

import org.example.datasource.model.GameStorage;
import org.example.datasource.repository.GamePlay;
import org.example.datasource.repository.GameRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameConfig {

    @Bean
    public GameStorage gameStorage() {
        return new GameStorage();
    }

    @Bean
    public GameRepository gameRepository(GameStorage gameStorage) {
        return new GameRepository(gameStorage);
    }

    @Bean
    public GamePlay gamePlay(GameRepository gameRepository) {
        return new GamePlay(gameRepository);
    }
}
