package org.example.domain.model;

import java.util.UUID;

public class User {
    private UUID userId;
    private String userLogin;
    private String userPasswordHash;

    public User() {
        this.userId = UUID.randomUUID();
    }

    public User(UUID userId, String userLogin) {
        this.userId = userId;
        this.userLogin = userLogin;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserPasswordHash() {
        return userPasswordHash;
    }

    public void setUserPasswordHash(String userPassword) {
        this.userPasswordHash = userPassword;
    }
}
