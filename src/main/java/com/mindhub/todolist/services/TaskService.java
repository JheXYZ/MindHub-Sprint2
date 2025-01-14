package com.mindhub.todolist.services;

import com.mindhub.todolist.dtos.task.NewTaskRequestDTO;
import com.mindhub.todolist.dtos.task.PatchTaskRequestDTO;
import com.mindhub.todolist.dtos.task.PutTaskRequestDTO;
import com.mindhub.todolist.exceptions.InvalidTaskException;
import com.mindhub.todolist.exceptions.TaskNotFoundException;
import com.mindhub.todolist.exceptions.UnauthorizedException;
import com.mindhub.todolist.exceptions.UserNotFoundException;
import com.mindhub.todolist.models.Task;

import java.util.List;

public interface TaskService {

    List<Task> getAllTasks();

    Task getTaskById(Long id) throws TaskNotFoundException;

    Task createTask(String email, NewTaskRequestDTO newTaskRequestDTO) throws UserNotFoundException, UnauthorizedException, InvalidTaskException;

    void deleteTask(Long id) throws UnauthorizedException, TaskNotFoundException;

    Task updatePutTask(Long id, PutTaskRequestDTO putTaskRequestDTO) throws UnauthorizedException, InvalidTaskException, TaskNotFoundException;

    Task updatePatchTask(Long id, PatchTaskRequestDTO PatchTaskRequestDTO) throws UnauthorizedException, InvalidTaskException, TaskNotFoundException;

}
