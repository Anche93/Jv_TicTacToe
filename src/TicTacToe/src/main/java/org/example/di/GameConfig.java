package org.example.di;

import org.example.domain.port.GameRepositoryPort;
import org.example.domain.port.MoveStrategy;
import org.example.domain.service.GameService;
import org.example.domain.service.GameServiceImp;
import org.example.domain.service.MinimaxStrategy;
import org.example.domain.service.WinChecker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class GameConfig {

    @Bean
    public WinChecker winChecker() {
        return new WinChecker();
    }

    @Bean
    public MoveStrategy moveStrategy(WinChecker winChecker) {
        return new MinimaxStrategy(winChecker);
    }

    @Bean
    @Transactional
    public GameService gameService(GameRepositoryPort gameRepositoryPort,
                                   MoveStrategy moveStrategy,
                                   WinChecker winChecker) {
        return new GameServiceImp(gameRepositoryPort, moveStrategy, winChecker);
    }
}
