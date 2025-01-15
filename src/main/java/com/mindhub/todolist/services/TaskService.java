package com.mindhub.todolist.services;

import com.mindhub.todolist.dtos.task.*;
import com.mindhub.todolist.exceptions.InvalidTaskException;
import com.mindhub.todolist.exceptions.TaskNotFoundException;
import com.mindhub.todolist.exceptions.UnauthorizedException;
import com.mindhub.todolist.exceptions.UserNotFoundException;
import com.mindhub.todolist.models.Task;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TaskService {

    List<Task> getAllTasks();

    Task getTaskById(Long id) throws TaskNotFoundException;

    Task createTask(String email, NewTaskRequestDTO newTaskRequestDTO) throws UserNotFoundException, UnauthorizedException, InvalidTaskException;

    void deleteTask(Long id) throws UnauthorizedException, TaskNotFoundException;

    Task updatePutTask(Long id, PutTaskRequestDTO putTaskRequestDTO) throws UnauthorizedException, InvalidTaskException, TaskNotFoundException;

    Task updatePatchTask(Long id, PatchTaskRequestDTO PatchTaskRequestDTO) throws UnauthorizedException, InvalidTaskException, TaskNotFoundException;

    ResponseEntity<List<TaskDTO>> getAllTasksRequest();

    ResponseEntity<List<TaskUserDTO>> getTasksRequest(String name) throws UserNotFoundException;

    ResponseEntity<TaskDTO> getTaskByIdRequest(Long id) throws TaskNotFoundException;

    ResponseEntity<TaskUserDTO> getTaskFromUserByIdRequest(String name, Long id) throws UserNotFoundException, TaskNotFoundException;

    ResponseEntity<TaskDTO> createTaskByUserIdRequest(Long userId, NewTaskRequestDTO taskRequestDTO) throws UserNotFoundException, InvalidTaskException;

    ResponseEntity<TaskUserDTO> createTaskRequest(String name, NewTaskRequestDTO newTaskRequestDTO) throws UserNotFoundException, InvalidTaskException;

    ResponseEntity<?> deleteTaskByIdRequest(Long id) throws TaskNotFoundException;

    ResponseEntity<?> deleteTaskFromUserRequest(String name, Long id) throws UserNotFoundException, TaskNotFoundException;

    ResponseEntity<TaskDTO> updatePutTaskByIdRequest(Long id, PutTaskRequestDTO putTaskRequestDTO) throws InvalidTaskException, TaskNotFoundException;

    ResponseEntity<TaskUserDTO> updatePutTaskFromUserRequest(String name, Long id, PutTaskRequestDTO putTaskRequestDTO) throws TaskNotFoundException, InvalidTaskException;

    ResponseEntity<TaskDTO> updatePatchTaskByIdRequest(Long id, PatchTaskRequestDTO patchTaskRequestDTO) throws InvalidTaskException, TaskNotFoundException;

    ResponseEntity<TaskUserDTO> updatePatchTaskFromUserRequest(String name, Long id, PatchTaskRequestDTO patchTaskRequestDTO) throws UserNotFoundException, TaskNotFoundException, InvalidTaskException;
}
