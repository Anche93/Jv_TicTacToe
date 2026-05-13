package org.example.datasource.repository;

import org.example.datasource.mapper.UserDataMapper;
import org.example.datasource.model.RoleEntity;
import org.example.datasource.model.UserEntity;
import org.example.domain.model.User;
import org.example.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;

    public UserRepositoryAdapter(
            UserJpaRepository userJpaRepository,
            RoleJpaRepository roleJpaRepository) {
        this.userJpaRepository = userJpaRepository;
        this.roleJpaRepository = roleJpaRepository;
    }

    @Override
    public Optional<User> findByUserLogin(String login) {
        return userJpaRepository.findByUserLogin(login).map(UserDataMapper::toDomain);
    }

    @Override
    public Optional<User> findByUserId(UUID userId) {
        return userJpaRepository.findById(userId).map(UserDataMapper::toDomain);
    }

    @Override
    public void save(User user) {
        Set<RoleEntity> roleEntitySet =  user.getRoles()
                .stream()
                .map(role -> roleJpaRepository
                                .findByName(role.getName())
                                .orElseThrow(() -> new RuntimeException("Роль не найдена")))
                .collect(Collectors.toSet());
        UserEntity entity = UserDataMapper.toEntity(user);
        entity.setRoles(roleEntitySet);
        userJpaRepository.save(entity);
    }

    @Override
    public boolean existsByUserLogin(String login) {
        return userJpaRepository.existsByUserLogin(login);
    }
}
