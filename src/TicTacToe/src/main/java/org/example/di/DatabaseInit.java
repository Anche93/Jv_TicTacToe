package org.example.di;

import org.example.datasource.model.RoleEntity;
import org.example.datasource.repository.RoleJpaRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInit {

    private final RoleJpaRepository roleJpaRepository;

    public DatabaseInit(RoleJpaRepository roleJpaRepository) {
        this.roleJpaRepository = roleJpaRepository;
    }


    @EventListener(ApplicationReadyEvent.class)
    public void initRoles() {
        if (roleJpaRepository.findByName("USER").isEmpty()) {
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName("USER");
            roleJpaRepository.save(roleEntity);
        }
    }
}
