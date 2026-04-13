package org.example.datasource.mapper;

import org.example.datasource.model.UserEntity;
import org.example.domain.model.User;

public class UserDataMapper {

    public static UserEntity toEntity(User user) {
        return new UserEntity(user.getUserId(), user.getUserLogin(), user.getUserPasswordHash());
    }

    public static User toDomain(UserEntity entity) {
        return new User(entity.getUserId(), entity.getUserLogin());
    }
}
