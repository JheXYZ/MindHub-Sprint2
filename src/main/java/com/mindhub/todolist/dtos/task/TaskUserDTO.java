package com.mindhub.todolist.dtos.task;

import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.TaskStatus;

public class TaskUserDTO {

    private final Long id;
    private final String title;
    private final String description;
    private final TaskStatus taskStatus;

    public TaskUserDTO(Long id, String title, String description, TaskStatus taskStatus) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.taskStatus = taskStatus;
    }

    public TaskUserDTO(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.taskStatus = task.getTaskStatus();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }
}
