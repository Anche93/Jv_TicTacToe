package org.example.web.mapper;

import org.example.datasource.model.UserEntity;
import org.example.domain.model.User;
import org.example.web.model.UserDto;

public class UserWebMapper {
    public static UserDto toDto(User user) {
        if (user == null) return null;
        return new UserDto(user.getUserId(), user.getUserLogin());
    }

    public static UserDto toDto(UserEntity userEntity) {
        if (userEntity == null) return null;
        return new UserDto(userEntity.getUserId(), userEntity.getUserLogin());
    }
}
