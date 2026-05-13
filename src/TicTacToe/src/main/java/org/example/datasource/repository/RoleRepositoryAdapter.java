package org.example.datasource.repository;

import org.example.datasource.mapper.RoleDataMapper;
import org.example.domain.model.Role;
import org.example.domain.port.RoleRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository jpaRepository;

    public RoleRepositoryAdapter(RoleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Role> findByName(String name) {
        return jpaRepository.findByName(name).map(RoleDataMapper::toDomain);
    }
}
