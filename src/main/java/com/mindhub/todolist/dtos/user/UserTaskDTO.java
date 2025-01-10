package com.mindhub.todolist.dtos.user;

import com.mindhub.todolist.models.UserEntity;

public class UserTaskDTO{

    private final Long id;
    private final String username;
    private final String email;

    public UserTaskDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public UserTaskDTO(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.username = userEntity.getUsername();
        this.email = userEntity.getEmail();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}