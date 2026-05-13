package org.example.datasource.mapper;

import org.example.datasource.model.RoleEntity;
import org.example.domain.model.Role;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RoleDataMapper {

    public static RoleEntity toEntity(Role role) {
        return new RoleEntity(role.getName());
    }

    public static Role toDomain(RoleEntity roleEntity) {
        return Role.valueOf(roleEntity.getName());
    }

    public static Set<RoleEntity> toEntitySet(List<Role> roleList) {
        return roleList
                .stream()
                .map(RoleDataMapper::toEntity)
                .collect(Collectors.toSet());
    }

    public static List<Role> toDomainList(Set<RoleEntity> roleEntitySet) {
        return roleEntitySet
                .stream()
                .map(RoleDataMapper::toDomain)
                .toList();
    }
}
