package com.mindhub.todolist.services;

import com.mindhub.todolist.dtos.task.NewTaskRequestDTO;
import com.mindhub.todolist.dtos.task.PatchTaskRequestDTO;
import com.mindhub.todolist.dtos.task.PutTaskRequestDTO;
import com.mindhub.todolist.dtos.task.TaskDTO;
import com.mindhub.todolist.dtos.user.NewUserRequestDTO;
import com.mindhub.todolist.dtos.user.PatchUserRequestDTO;
import com.mindhub.todolist.dtos.user.UserTaskRequestDTO;
import com.mindhub.todolist.exceptions.*;
import com.mindhub.todolist.models.Task;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TaskService {
    ResponseEntity<List<TaskDTO>> getAllTasksDTO();

    ResponseEntity<TaskDTO> getTaskDTOById(Long id) throws TaskNotFoundException;

    ResponseEntity<TaskDTO> createTask(NewTaskRequestDTO newTaskRequestDTO) throws UserNotFoundException, UnauthorizedException, InvalidTaskException;

    ResponseEntity<?> deleteTask(Long id, UserTaskRequestDTO userTaskRequestDTO) throws UnauthorizedException;

    ResponseEntity<TaskDTO> updatePutTask(Long id, PutTaskRequestDTO putTaskRequestDTO) throws UnauthorizedException, InvalidTaskException;

    ResponseEntity<TaskDTO> updatePatchTask(Long id, PatchTaskRequestDTO PatchTaskRequestDTO) throws UnauthorizedException, InvalidTaskException;
}
