package org.example.domain.port;

import org.example.domain.model.Role;

import java.util.Optional;

public interface RoleRepositoryPort {

    Optional<Role> findByName(String name);

}
