package com.mindhub.todolist.dtos.task;

import com.mindhub.todolist.dtos.user.UserTaskDTO;
import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.TaskStatus;

import java.util.Arrays;

public class TaskDTO {

    private final Long id;
    private final String title, description;
    private final TaskStatus taskStatus;
    private final UserTaskDTO userTaskDTO;

    public TaskDTO(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.taskStatus = task.getTaskStatus();
        this.userTaskDTO = new UserTaskDTO(task.getUser());
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

    public UserTaskDTO getUser() {
        return userTaskDTO;
    }

    private boolean isValidTask() {
        return Arrays.stream(TaskStatus.values()).parallel().anyMatch(taskStatus::equals);
    }

}
