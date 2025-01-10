package com.mindhub.todolist.services.implementations;

import com.mindhub.todolist.dtos.task.NewTaskRequestDTO;
import com.mindhub.todolist.dtos.task.PatchTaskRequestDTO;
import com.mindhub.todolist.dtos.task.PutTaskRequestDTO;
import com.mindhub.todolist.dtos.task.TaskDTO;
import com.mindhub.todolist.dtos.user.PatchUserRequestDTO;
import com.mindhub.todolist.dtos.user.UserTaskRequestDTO;
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
    public ResponseEntity<List<TaskDTO>> getAllTasksDTO() {
        return ResponseEntity.ok(taskRepository.findAll().stream().map(TaskDTO::new).toList());
    }

    @Override
    public ResponseEntity<TaskDTO> getTaskDTOById(Long id) throws TaskNotFoundException {
        return ResponseEntity.ok(
                new TaskDTO(taskRepository
                        .findById(id)
                        .orElseThrow(TaskNotFoundException::new))
        );
    }

    @Override
    public ResponseEntity<TaskDTO> createTask(NewTaskRequestDTO newTaskRequestDTO) throws UserNotFoundException, UnauthorizedException, InvalidTaskException {
        UserEntity user = userService.findUserByEmail(newTaskRequestDTO.user().email())
                .orElseThrow(() -> new UserNotFoundException("email or password are incorrect"));

        validateRequest(user, newTaskRequestDTO);

        Task task = new Task(newTaskRequestDTO.title(),
                newTaskRequestDTO.description(),
                newTaskRequestDTO.taskStatus() != null ? newTaskRequestDTO.taskStatus() : TaskStatus.PENDING);
        user.addTask(task);
        return new ResponseEntity<>(new TaskDTO(taskRepository.save(task)), HttpStatus.CREATED) ;
    }

    @Override
    public ResponseEntity<?> deleteTask(Long id, UserTaskRequestDTO userTaskRequestDTO) throws UnauthorizedException {
        UserEntity user = userService.findUserByEmail(userTaskRequestDTO.email())
                .orElseThrow(() -> new UnauthorizedException("email or password are incorrect"));

        validateRequest(user, userTaskRequestDTO);
        Task task = getTaskInUser(user, id);

        taskRepository.delete(task);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<TaskDTO> updatePutTask(Long id, PutTaskRequestDTO putTaskRequestDTO) throws UnauthorizedException, InvalidTaskException {
        UserTaskRequestDTO userRequest = putTaskRequestDTO.user();
        UserEntity user = userService.findUserByEmail(userRequest.email())
                .orElseThrow(() -> new UnauthorizedException("email or password are incorrect"));

        validateRequest(user, userRequest);
        Task task = getTaskInUser(user, id);
        makeUpdatesPutTask(task, putTaskRequestDTO);

        return ResponseEntity.ok(new TaskDTO(taskRepository.save(task)));
    }

    @Override
    public ResponseEntity<TaskDTO> updatePatchTask(Long id, PatchTaskRequestDTO patchUserRequestDTO) throws UnauthorizedException, InvalidTaskException {
        UserTaskRequestDTO userRequest = patchUserRequestDTO.user();
        UserEntity user = userService.findUserByEmail(userRequest.email())
                .orElseThrow(() -> new UnauthorizedException("email or password are incorrect"));

        validateRequest(user, userRequest);
        Task task = getTaskInUser(user, id);
        makeUpdatesPatchTask(task, patchUserRequestDTO);

        return ResponseEntity.ok(new TaskDTO(taskRepository.save(task)));
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
        task.setTitle(task.getTitle());
        task.setDescription(taskUpdate.description());
        task.setTaskStatus(taskUpdate.taskStatus());
    }

    private void validateRequest(UserEntity user, NewTaskRequestDTO newTaskRequestDTO) throws UnauthorizedException, InvalidTaskException {
        UserTaskRequestDTO userRequest = newTaskRequestDTO.user();
        validateCredentials(
                user.getEmail(),
                user.getPassword(),
                userRequest.email(),
                userRequest.password());
        validateTitleAndDescription(newTaskRequestDTO.title(), newTaskRequestDTO.description());
    }

    private void validateRequest(UserEntity user, UserTaskRequestDTO userTaskRequestDTO) throws UnauthorizedException {
        validateCredentials(
                user.getEmail(),
                user.getPassword(),
                userTaskRequestDTO.email(),
                userTaskRequestDTO.password());
    }

    private void validateCredentials(String validEmail, String validPassword, String email, String password) throws UnauthorizedException {
        if (!validEmail.equals(email) || !validPassword.equals(password))
            throw new UnauthorizedException("email or password are incorrect");
    }

    private void validateTitleAndDescription(String title, String description) throws InvalidTaskException {
        if (title.isBlank() && description.isBlank())
            throw new InvalidTaskException("either title or description must have text");
    }

    private Task getTaskInUser(UserEntity user, Long id) throws UnauthorizedException {
        return user.getTasks()
                .stream()
                .filter(currTask -> currTask.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedException("unauthorized user"));
    }
}
