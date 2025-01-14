package com.mindhub.todolist.dtos.task;

import com.mindhub.todolist.models.TaskStatus;

public record NewTaskRequestDTO(
        String title,
        String description,
        TaskStatus taskStatus
) {
}
