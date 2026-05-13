package org.example.datasource.mapper;

import org.example.datasource.model.RoleEntity;
import org.example.datasource.model.UserEntity;
import org.example.domain.model.Role;
import org.example.domain.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserDataMapperTest {

    @Test
    void testToEntity_ShouldMapAllFields() {
        User domain = new User(
                UUID.randomUUID(),
                "TestUser",
                "123PasswordHash",
                List.of(Role.USER)
        );
        UserEntity entity = UserDataMapper.toEntity(domain);

        assertEquals(domain.getUserId(), entity.getUserId());
        assertEquals(domain.getUserLogin(), entity.getUserLogin());
        assertEquals(domain.getUserPasswordHash(), entity.getUserPasswordHash());
        assertNotNull(entity.getRoles());
        assertTrue(entity.getRoles().stream().anyMatch(r -> r.getName().equals("USER")));
    }

    @Test
    void testToDomain_ShouldMapAllFields() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("USER");

        UserEntity entity = new UserEntity(
                UUID.randomUUID(),
                "TestUser",
                "123PasswordHash",
                Set.of(roleEntity)
        );
        User domain = UserDataMapper.toDomain(entity);

        assertEquals(entity.getUserId(), domain.getUserId());
        assertEquals(entity.getUserLogin(), domain.getUserLogin());
        assertEquals(entity.getUserPasswordHash(), domain.getUserPasswordHash());
        assertEquals(1, domain.getRoles().size());
        assertEquals(Role.USER, domain.getRoles().getFirst());
    }

    @Test
    void testToDomain_ShouldHandleEmptyRoles() {
        UserEntity entity = new UserEntity(
                UUID.randomUUID(),
                "TestUser",
                "123PasswordHash",
                Set.of()
        );
        User domain = UserDataMapper.toDomain(entity);

        assertNotNull(domain.getRoles());
        assertTrue(domain.getRoles().isEmpty());
    }
}
