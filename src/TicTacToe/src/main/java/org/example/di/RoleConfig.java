package org.example.di;

import org.example.datasource.repository.RoleJpaRepository;
import org.example.datasource.repository.RoleRepositoryAdapter;
import org.example.domain.port.RoleRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleConfig {

    @Bean
    public RoleRepositoryPort roleRepositoryPort(RoleJpaRepository jpaRepository) {
        return new RoleRepositoryAdapter(jpaRepository);
    }
}
