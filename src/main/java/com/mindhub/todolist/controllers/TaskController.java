package com.mindhub.todolist.controllers;

import com.mindhub.todolist.dtos.task.NewTaskRequestDTO;
import com.mindhub.todolist.dtos.task.PatchTaskRequestDTO;
import com.mindhub.todolist.dtos.task.PutTaskRequestDTO;
import com.mindhub.todolist.dtos.task.TaskDTO;
import com.mindhub.todolist.dtos.user.NewUserRequestDTO;
import com.mindhub.todolist.dtos.user.PatchUserRequestDTO;
import com.mindhub.todolist.dtos.user.UserDTO;
import com.mindhub.todolist.dtos.user.UserTaskRequestDTO;
import com.mindhub.todolist.exceptions.*;
import com.mindhub.todolist.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return taskService.getAllTasksDTO();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Long id) throws TaskNotFoundException {
        return taskService.getTaskDTOById(id);
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody NewTaskRequestDTO newTaskRequestDTO) throws UserNotFoundException, UnauthorizedException, InvalidTaskException {
        return taskService.createTask(newTaskRequestDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, @Valid @RequestBody UserTaskRequestDTO userTaskRequestDTO) throws UnauthorizedException {
        return taskService.deleteTask(id, userTaskRequestDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updatePutTask(@PathVariable Long id, @Valid @RequestBody PutTaskRequestDTO putTaskRequestDTO) throws InvalidTaskException, UnauthorizedException {
        return taskService.updatePutTask(id, putTaskRequestDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskDTO> updatePatchTask(@PathVariable Long id, @Valid @RequestBody PatchTaskRequestDTO patchTaskRequestDTO) throws InvalidTaskException, UnauthorizedException {
        return taskService.updatePatchTask(id, patchTaskRequestDTO);
    }
}
