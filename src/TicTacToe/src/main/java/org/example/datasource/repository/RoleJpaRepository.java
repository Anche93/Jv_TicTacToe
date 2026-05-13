package org.example.datasource.repository;

import org.example.datasource.model.RoleEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleJpaRepository extends CrudRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(String name);

}
