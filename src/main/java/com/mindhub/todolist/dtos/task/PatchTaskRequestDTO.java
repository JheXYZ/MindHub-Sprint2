package com.mindhub.todolist.dtos.task;

import com.mindhub.todolist.models.TaskStatus;

public record PatchTaskRequestDTO(
        String title,
        String description,
        TaskStatus taskStatus
) {
}
