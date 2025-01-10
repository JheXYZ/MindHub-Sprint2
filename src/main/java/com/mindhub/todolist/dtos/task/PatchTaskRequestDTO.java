package com.mindhub.todolist.dtos.task;

import com.mindhub.todolist.dtos.user.UserTaskRequestDTO;
import com.mindhub.todolist.models.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record PatchTaskRequestDTO(
        String title,
        String description,
        TaskStatus taskStatus,
        @NotNull(message = "user must be provided")
        UserTaskRequestDTO user
) {
}
