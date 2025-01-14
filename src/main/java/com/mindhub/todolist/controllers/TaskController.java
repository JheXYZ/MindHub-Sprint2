package com.mindhub.todolist.controllers;

import com.mindhub.todolist.dtos.task.*;
import com.mindhub.todolist.exceptions.InvalidTaskException;
import com.mindhub.todolist.exceptions.TaskNotFoundException;
import com.mindhub.todolist.exceptions.UnauthorizedException;
import com.mindhub.todolist.exceptions.UserNotFoundException;
import com.mindhub.todolist.services.implementations.TaskServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    @Autowired
    private TaskServiceImp taskService;

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return taskService.getAllTasksRequest();
    }

    @GetMapping("/user")
    public ResponseEntity<List<TaskUserDTO>> getTasks(Authentication authentication) throws UserNotFoundException {
        return taskService.getTasksRequest(authentication.getName());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) throws TaskNotFoundException {
        return taskService.getTaskByIdRequest(id);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<TaskUserDTO> getTask(Authentication authentication, @PathVariable Long id) throws UserNotFoundException, TaskNotFoundException {
        return taskService.getTaskFromUserByIdRequest(authentication.getName(), id);
    }

    @PostMapping("/{id}")
    public ResponseEntity<TaskDTO> createTaskByUserId(@PathVariable Long userId, @Valid @RequestBody NewTaskRequestDTO taskRequestDTO) throws UserNotFoundException, InvalidTaskException, UnauthorizedException {
        return taskService.createTaskByUserIdRequest(userId, taskRequestDTO);
    }

    @PostMapping("/user")
    public ResponseEntity<TaskUserDTO> createTaskToUser(Authentication authentication, @Valid @RequestBody NewTaskRequestDTO newTaskRequestDTO) throws UserNotFoundException, UnauthorizedException, InvalidTaskException {
        return taskService.createTaskRequest(authentication.getName(), newTaskRequestDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaskById(@PathVariable Long id) throws TaskNotFoundException {
        return taskService.deleteTaskByIdRequest(id);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteTask(Authentication authentication, @PathVariable Long id) throws UserNotFoundException, TaskNotFoundException {
        return taskService.deleteTaskFromUserRequest(authentication.getName(), id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updatePutTaskById(@PathVariable Long id, @Valid @RequestBody PutTaskRequestDTO putTaskRequestDTO) throws InvalidTaskException, TaskNotFoundException {
        return taskService.updatePutTaskByIdRequest(id, putTaskRequestDTO);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<TaskUserDTO> updatePutTask(Authentication authentication, @PathVariable Long id, @Valid @RequestBody PutTaskRequestDTO putTaskRequestDTO) throws InvalidTaskException, TaskNotFoundException {
        return taskService.updatePutTaskFromUserRequest(authentication.getName(), id, putTaskRequestDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskDTO> updatePatchTaskById(@PathVariable Long id, @Valid @RequestBody PatchTaskRequestDTO patchTaskRequestDTO) throws InvalidTaskException, TaskNotFoundException {
        return taskService.updatePatchTaskByIdRequest(id, patchTaskRequestDTO);
    }

    @PatchMapping("/user/{id}")
    public ResponseEntity<TaskUserDTO> updatePatchTask(Authentication authentication, @PathVariable Long id, @RequestBody PatchTaskRequestDTO patchTaskRequestDTO) throws UserNotFoundException, InvalidTaskException, TaskNotFoundException {
        return taskService.updatePatchTaskFromUserRequest(authentication.getName(), id, patchTaskRequestDTO);
    }
}
