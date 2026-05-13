package org.example.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private UUID userId;
    private String userLogin;
    private String userPasswordHash;
    private List<Role> roles;

    public User() {
        this.userId = UUID.randomUUID();
    }

    public User(UUID userId, String userLogin) {
        this.userId = userId;
        this.userLogin = userLogin;
    }

    public void assignDefaultRole() {
        this.roles = List.of(Role.USER);
    }
}
