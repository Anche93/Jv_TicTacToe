package org.example.datasource.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Entity
public class UserEntity {

    @Id
    @Column(name = "User_ID")
    private UUID userId;

    @Column(name = "User_Login", unique = true, nullable = false)
    private String userLogin;

    @Column(name = "User_password", nullable = false)
    private String userPasswordHash;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();
}
