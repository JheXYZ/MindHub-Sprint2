package com.mindhub.todolist.services.implementations;

import com.mindhub.todolist.dtos.task.*;
import com.mindhub.todolist.exceptions.InvalidTaskException;
import com.mindhub.todolist.exceptions.TaskNotFoundException;
import com.mindhub.todolist.exceptions.UnauthorizedException;
import com.mindhub.todolist.exceptions.UserNotFoundException;
import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.TaskStatus;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.TaskRepository;
import com.mindhub.todolist.services.TaskService;
import com.mindhub.todolist.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImp implements TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserService userService;

    @Override
    public ResponseEntity<List<TaskDTO>> getAllTasksRequest() {
        return ResponseEntity.ok(getAllTasks()
                        .stream()
                        .map(TaskDTO::new)
                        .toList());
    }

    @Override
    public ResponseEntity<List<TaskUserDTO>> getTasksRequest(String email) throws UserNotFoundException {
        return ResponseEntity.ok(taskRepository
                .findByUser_email(email).stream()
                .map(TaskUserDTO::new)
                .toList());
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public ResponseEntity<TaskDTO> getTaskByIdRequest(Long id) throws TaskNotFoundException {
        return ResponseEntity.ok(new TaskDTO(getTaskById(id)));
    }

    @Override
    public ResponseEntity<TaskUserDTO> getTaskFromUserByIdRequest(String name, Long id) throws TaskNotFoundException {
        Task task = getTaskById(id);
        if (!task.getUser().getEmail().equals(name))
            throw new TaskNotFoundException();
        return ResponseEntity.ok(new TaskUserDTO(task));
    }

    @Override
    public Task getTaskById(Long id) throws TaskNotFoundException {
        return taskRepository
                .findById(id)
                .orElseThrow(TaskNotFoundException::new);
    }

    public ResponseEntity<TaskUserDTO> createTaskRequest(String email, NewTaskRequestDTO newTaskRequestDTO) throws UserNotFoundException, InvalidTaskException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new TaskUserDTO(createTask(email, newTaskRequestDTO)));
    }

    public ResponseEntity<TaskDTO> createTaskByUserIdRequest(Long userId, NewTaskRequestDTO newTaskRequestDTO) throws UserNotFoundException, InvalidTaskException {
        String email = userService.getUserById(userId).getEmail();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new TaskDTO(createTask(email, newTaskRequestDTO)));
    }

    @Override
    public Task createTask(String email, NewTaskRequestDTO newTaskRequestDTO) throws UserNotFoundException, InvalidTaskException {
        UserEntity user = userService.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("email or password are incorrect"));

        if (newTaskRequestDTO.title() == null || newTaskRequestDTO.description() == null)
            throw new InvalidTaskException("title and description must be provided");
        validateTitleAndDescription(newTaskRequestDTO.title(), newTaskRequestDTO.description());
        return taskRepository.save(
                new Task(
                    newTaskRequestDTO.title(),
                    newTaskRequestDTO.description(),
                    newTaskRequestDTO.taskStatus() != null ? newTaskRequestDTO.taskStatus() : TaskStatus.PENDING,
                    user));
    }

    public ResponseEntity<?> deleteTaskByIdRequest(Long id) throws TaskNotFoundException {
        deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> deleteTaskFromUserRequest(String email, Long id) throws UserNotFoundException, TaskNotFoundException {
        if (!taskRepository.existsByIdAndUser_email(id, email))
            throw new TaskNotFoundException();
        deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public void deleteTask(Long id) throws TaskNotFoundException {
        Task task = taskRepository.findById(id)
                .orElseThrow(TaskNotFoundException::new);
        taskRepository.delete(task);
    }

    public ResponseEntity<TaskDTO> updatePutTaskByIdRequest(Long id, PutTaskRequestDTO putTaskRequestDTO) throws InvalidTaskException, TaskNotFoundException {
        return ResponseEntity.ok(new TaskDTO(updatePutTask(id, putTaskRequestDTO)));
    }

    public ResponseEntity<TaskUserDTO> updatePutTaskFromUserRequest(String email, Long id, PutTaskRequestDTO putTaskRequestDTO) throws TaskNotFoundException, InvalidTaskException {
        if(!taskRepository.existsByIdAndUser_email(id, email))
            throw new TaskNotFoundException();
        return ResponseEntity.ok(new TaskUserDTO(updatePutTask(id, putTaskRequestDTO)));
    }

    @Override
    public Task updatePutTask(Long id, PutTaskRequestDTO putTaskRequestDTO) throws InvalidTaskException, TaskNotFoundException {
        Task task = taskRepository.findById(id)
                .orElseThrow(TaskNotFoundException::new);

        if (putTaskRequestDTO.title() == null || putTaskRequestDTO.description() == null)
            throw new InvalidTaskException("title and description must be provided");
        makeUpdatesPutTask(task, putTaskRequestDTO);
        return taskRepository.save(task);
    }

    public ResponseEntity<TaskDTO> updatePatchTaskByIdRequest(Long id, PatchTaskRequestDTO patchTaskRequestDTO) throws InvalidTaskException, TaskNotFoundException {
        return ResponseEntity.ok(new TaskDTO(updatePatchTask(id, patchTaskRequestDTO)));
    }

    public ResponseEntity<TaskUserDTO> updatePatchTaskFromUserRequest(String email, Long id, PatchTaskRequestDTO patchTaskRequestDTO) throws UserNotFoundException, TaskNotFoundException, InvalidTaskException {
        if(!taskRepository.existsByIdAndUser_email(id, email))
            throw new TaskNotFoundException();

        return ResponseEntity.ok(new TaskUserDTO(updatePatchTask(id, patchTaskRequestDTO)));
    }

    @Override
    public Task updatePatchTask(Long id, PatchTaskRequestDTO patchUserRequestDTO) throws InvalidTaskException, TaskNotFoundException {;
        Task task = taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);

        makeUpdatesPatchTask(task, patchUserRequestDTO);
        return taskRepository.save(task);
    }

    private void makeUpdatesPatchTask(Task task, PatchTaskRequestDTO taskUpdate) throws InvalidTaskException {
        if (taskUpdate.title() == null && taskUpdate.description() == null && taskUpdate.taskStatus() == null)
            throw new InvalidTaskException("at least one field of the task must be provided");
        if (taskUpdate.title() != null)
            task.setTitle(taskUpdate.title());
        if (taskUpdate.description() != null)
            task.setDescription(taskUpdate.description());
        if (taskUpdate.title() != null && taskUpdate.description() != null)
            validateTitleAndDescription(taskUpdate.title(), taskUpdate.description());
        if (taskUpdate.taskStatus() != null)
            task.setTaskStatus(taskUpdate.taskStatus());
    }

    private void makeUpdatesPutTask(Task task, PutTaskRequestDTO taskUpdate) throws InvalidTaskException {
        validateTitleAndDescription(taskUpdate.title(), taskUpdate.description());
        task.setTitle(taskUpdate.title());
        task.setDescription(taskUpdate.description());
        task.setTaskStatus(taskUpdate.taskStatus());
    }

    private void validateTitleAndDescription(String title, String description) throws InvalidTaskException {
        if (title.isBlank() && description.isBlank())
            throw new InvalidTaskException("either title or description must have text");
    }

}
