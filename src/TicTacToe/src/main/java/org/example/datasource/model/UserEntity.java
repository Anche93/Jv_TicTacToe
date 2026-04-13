package org.example.datasource.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Table(name = "users")
@Entity
public class UserEntity {

    @Id
    @Column(name = "User_ID")
    private UUID userId;

    @Column(name = "User_Login", unique = true, nullable = false)
    private String userLogin;

    @Column(name = "User_password", nullable = false)
    private String passwordHash;

    public UserEntity() {
    }

    public UserEntity(UUID userId, String userLogin, String passwordHash) {
        this.userId = userId;
        this.userLogin = userLogin;
        this.passwordHash = passwordHash;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
