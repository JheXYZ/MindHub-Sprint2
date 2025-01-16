package com.mindhub.todolist.controllers;

import com.mindhub.todolist.dtos.task.*;
import com.mindhub.todolist.exceptions.InvalidTaskException;
import com.mindhub.todolist.exceptions.TaskNotFoundException;
import com.mindhub.todolist.exceptions.UnauthorizedException;
import com.mindhub.todolist.exceptions.UserNotFoundException;
import com.mindhub.todolist.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Operation(
            summary = "Returns all tasks. (admin auth is required)",
            description = "Retrieves all tasks.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tasks successfully returned."),
                    @ApiResponse(responseCode = "403", description = "Access denied.")
            }
    )
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return taskService.getAllTasksRequest();
    }

    @Operation(
            summary = "Returns all tasks from logged in user. (user or admin auth is required)",
            description = "Retrieves all tasks from user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tasks successfully returned."),
                    @ApiResponse(responseCode = "403", description = "Access denied.")
            }
    )
    @GetMapping("/user")
    public ResponseEntity<List<TaskUserDTO>> getTasks(Authentication authentication) throws UserNotFoundException {
        return taskService.getTasksRequest(authentication.getName());
    }

    @Operation(
            summary = "Returns a single task. (admin auth is required)",
            description = "Returns a single task by id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task successfully returned."),
                    @ApiResponse(responseCode = "404", description = "Task was not found."),
                    @ApiResponse(responseCode = "403", description = "Access denied.")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) throws TaskNotFoundException {
        return taskService.getTaskByIdRequest(id);
    }

    @Operation(
            summary = "Returns a single task from the user. (user or admin auth is required)",
            description = "Returns a single task by id from the logged in user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task successfully returned."),
                    @ApiResponse(responseCode = "404", description = "Task was not found."),
                    @ApiResponse(responseCode = "403", description = "Access denied.")
            }
    )
    @GetMapping("/user/{id}")
    public ResponseEntity<TaskUserDTO> getTask(Authentication authentication, @PathVariable Long id) throws UserNotFoundException, TaskNotFoundException {
        return taskService.getTaskFromUserByIdRequest(authentication.getName(), id);
    }

    @Operation(
            summary = "Creates a task for a user. (admin auth is required)",
            description = "Creates a task for a user by id. Returns the created task.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Task successfully created."),
                    @ApiResponse(responseCode = "400", description = "Invalid task provided."),
                    @ApiResponse(responseCode = "403", description = "Access denied.")
            }
    )
    @PostMapping("/user/{userId}")
    public ResponseEntity<TaskDTO> createTaskByUserId(@PathVariable Long userId, @Valid @RequestBody NewTaskRequestDTO taskRequestDTO) throws UserNotFoundException, InvalidTaskException {
        return taskService.createTaskByUserIdRequest(userId, taskRequestDTO);
    }

    @Operation(
            summary = "Creates a task for the user. (user or admin auth is required)",
            description = "Creates a task for the logged in user Returns the created task.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Task successfully created."),
                    @ApiResponse(responseCode = "400", description = "Invalid task provided."),
                    @ApiResponse(responseCode = "403", description = "Access denied.")
            }
    )
    @PostMapping("/user")
    public ResponseEntity<TaskUserDTO> createTaskToUser(Authentication authentication, @Valid @RequestBody NewTaskRequestDTO newTaskRequestDTO) throws UserNotFoundException, InvalidTaskException {
        return taskService.createTaskRequest(authentication.getName(), newTaskRequestDTO);
    }

    @Operation(
            summary = "Deletes task by id. (admin auth is required)",
            description = "Deletes task. Returns confirmation code.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Task successfully deleted."),
                    @ApiResponse(responseCode = "403", description = "Access denied."),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaskById(@PathVariable Long id) throws TaskNotFoundException {
        return taskService.deleteTaskByIdRequest(id);
    }

    @Operation(
            summary = "Deletes task by id from the user. (user or admin auth is required)",
            description = "Deletes task from logged in user. Returns confirmation code.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Task successfully deleted."),
                    @ApiResponse(responseCode = "403", description = "Access denied."),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            }
    )
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteTask(Authentication authentication, @PathVariable Long id) throws UserNotFoundException, TaskNotFoundException {
        return taskService.deleteTaskFromUserRequest(authentication.getName(), id);
    }

    @Operation(
            summary = "Updates all attributes of a task. (admin auth is required)",
            description = "Sets the task with the one provided. Returns the task updated.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Task successfully updated."),
                    @ApiResponse(responseCode = "400", description = "Invalid task provided."),
                    @ApiResponse(responseCode = "403", description = "Access denied."),
                    @ApiResponse(responseCode = "404", description = "Task not found.")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updatePutTaskById(@PathVariable Long id, @Valid @RequestBody PutTaskRequestDTO putTaskRequestDTO) throws InvalidTaskException, TaskNotFoundException {
        return taskService.updatePutTaskByIdRequest(id, putTaskRequestDTO);
    }

    @Operation(
            summary = "Updates all attributes of a task from the user. (user or admin auth is required)",
            description = "Sets the task from the logged in user with the one provided. Returns the task updated.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Task successfully updated."),
                    @ApiResponse(responseCode = "400", description = "Invalid task provided."),
                    @ApiResponse(responseCode = "403", description = "Access denied."),
                    @ApiResponse(responseCode = "404", description = "Task not found.")
            }
    )
    @PutMapping("/user/{id}")
    public ResponseEntity<TaskUserDTO> updatePutTask(Authentication authentication, @PathVariable Long id, @Valid @RequestBody PutTaskRequestDTO putTaskRequestDTO) throws InvalidTaskException, TaskNotFoundException {
        return taskService.updatePutTaskFromUserRequest(authentication.getName(), id, putTaskRequestDTO);
    }

    @Operation(
            summary = "Updates provided attributes of a task. (admin auth is required)",
            description = "Updates task with the attributes provided. Returns the task updated.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Task successfully updated."),
                    @ApiResponse(responseCode = "400", description = "Invalid task provided."),
                    @ApiResponse(responseCode = "403", description = "Access denied."),
                    @ApiResponse(responseCode = "404", description = "Task not found.")
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<TaskDTO> updatePatchTaskById(@PathVariable Long id, @Valid @RequestBody PatchTaskRequestDTO patchTaskRequestDTO) throws InvalidTaskException, TaskNotFoundException {
        return taskService.updatePatchTaskByIdRequest(id, patchTaskRequestDTO);
    }

    @Operation(
            summary = "Updates provided attributes of a task from the user. (user or admin auth is required)",
            description = "Updates task from the logged in user with the attributes provided. Returns the task updated.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Task successfully updated."),
                    @ApiResponse(responseCode = "400", description = "Invalid task provided."),
                    @ApiResponse(responseCode = "403", description = "Access denied."),
                    @ApiResponse(responseCode = "404", description = "Task not found.")
            }
    )
    @PatchMapping("/user/{id}")
    public ResponseEntity<TaskUserDTO> updatePatchTask(Authentication authentication, @PathVariable Long id, @RequestBody PatchTaskRequestDTO patchTaskRequestDTO) throws UserNotFoundException, InvalidTaskException, TaskNotFoundException {
        return taskService.updatePatchTaskFromUserRequest(authentication.getName(), id, patchTaskRequestDTO);
    }
}
