package com.mindhub.todolist.dtos.user;

import com.mindhub.todolist.dtos.task.TaskUserDTO;
import com.mindhub.todolist.models.UserAuthority;
import com.mindhub.todolist.models.UserEntity;

import java.util.List;

public class ExtendedUserDTO {
    private final Long id;
    private final String username, email;
    private final UserAuthority authority;
    private final List<TaskUserDTO> taskUserDTOS;

    public ExtendedUserDTO(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.username = userEntity.getUsername();
        this.email = userEntity.getEmail();
        this.taskUserDTOS = userEntity.getTasks().stream().map(TaskUserDTO::new).toList();
        this.authority = userEntity.getAuthority();
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

    public List<TaskUserDTO> getTasks() {
        return taskUserDTOS;
    }

    public UserAuthority getAuthority() {
        return authority;
    }
}
