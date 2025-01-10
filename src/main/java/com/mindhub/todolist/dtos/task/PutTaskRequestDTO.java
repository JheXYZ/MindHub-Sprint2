package com.mindhub.todolist.dtos.task;

import com.mindhub.todolist.dtos.user.UserTaskRequestDTO;
import com.mindhub.todolist.models.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record PutTaskRequestDTO(
        String title,
        String description,
        @NotNull(message = "task status must be provided")
        TaskStatus taskStatus,
        @NotNull(message = "user must be provided")
        UserTaskRequestDTO user
) {
}
