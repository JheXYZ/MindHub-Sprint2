package com.mindhub.todolist.dtos.task;

import com.mindhub.todolist.models.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record PutTaskRequestDTO(
        String title,
        String description,
        @NotNull(message = "task status must be provided")
        TaskStatus taskStatus
) {
}
