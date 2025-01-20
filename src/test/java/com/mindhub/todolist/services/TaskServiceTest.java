package com.mindhub.todolist.services;

import com.mindhub.todolist.dtos.task.NewTaskRequestDTO;
import com.mindhub.todolist.dtos.task.PatchTaskRequestDTO;
import com.mindhub.todolist.dtos.task.PutTaskRequestDTO;
import com.mindhub.todolist.exceptions.InvalidTaskException;
import com.mindhub.todolist.exceptions.TaskNotFoundException;
import com.mindhub.todolist.exceptions.UserNotFoundException;
import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.TaskStatus;
import com.mindhub.todolist.models.UserAuthority;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.TaskRepository;
import com.mindhub.todolist.services.implementations.TaskServiceImp;
import com.mindhub.todolist.services.implementations.UserServiceImp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class TaskServiceTest {

    @Autowired
    private TaskServiceImp taskService;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private UserServiceImp userService;

    // Creation test
    @Test
    public void shouldCreateTaskWithCorrectAttributesAndUser() {
        // Setup
        UserEntity user = new UserEntity("test@email.com", "test123", "test", UserAuthority.USER);
        NewTaskRequestDTO taskRequest = new NewTaskRequestDTO("Test title", "Test description", TaskStatus.PENDING);
        Task task = new Task(taskRequest.title(), taskRequest.description(), taskRequest.taskStatus(), user);

        when(userService.findUserByEmail(anyString()))
                .thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class)))
                .thenReturn(task);

        // Main test
        Task createdTask = assertDoesNotThrow(() -> taskService.createTask(user.getEmail(), taskRequest)) ;

        // Assert results
        assertNotNull(createdTask);
        assertNotNull(createdTask.getUser());
        assertEquals(user.getEmail(), createdTask.getUser().getEmail());
        assertEquals("Test title", createdTask.getTitle());
        assertEquals("Test description", createdTask.getDescription());
        assertEquals(TaskStatus.PENDING, createdTask.getTaskStatus());
    }

    // Creation test
    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExistWhileCreatingATask() {
        // Setup
        String email = "nonexistent@email.com";
        NewTaskRequestDTO taskRequest = new NewTaskRequestDTO("Test title", "Test description", TaskStatus.PENDING);

        when(userService.findUserByEmail(email))
                .thenReturn(Optional.empty());

        // Main test and Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> taskService.createTask(email, taskRequest)
        );
        assertEquals("invalid user", exception.getMessage());

        // Verify that findUserByEmail() was called and save() wasn't
        verify(userService).findUserByEmail(email);
        verify(taskRepository, never()).save(any(Task.class));
    }

    // Creation test
    @Test
    void shouldThrowInvalidTaskExceptionWhenTitleOrDescriptionIsNullWhileCreatingATask() {
        // Setup
        String email = "test@email.com";
        UserEntity user = new UserEntity(email, "password", "Test User", UserAuthority.USER);
        NewTaskRequestDTO invalidTaskRequest = new NewTaskRequestDTO(null, "Test description", TaskStatus.PENDING);

        when(userService.findUserByEmail(email))
                .thenReturn(Optional.of(user));

        // Main Test and Assert
        InvalidTaskException exception = assertThrows(
                InvalidTaskException.class,
                () -> taskService.createTask(email, invalidTaskRequest)
        );
        assertEquals("title and description must be provided", exception.getMessage());

        // Verify interactions
        verify(userService).findUserByEmail(email);
        verify(taskRepository, never()).save(any(Task.class));
    }

    // Deletion test
    @Test
    void shouldDeleteTaskSuccessfully() {
        // Setup
        Long taskId = 1L;
        Task task = new Task("Test title", "Test description", TaskStatus.PENDING, null);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        // Main test
        assertDoesNotThrow(() -> taskService.deleteTask(taskId));

        // Verifies that findById() and delete() were called
        verify(taskRepository).findById(taskId);
        verify(taskRepository).delete(task);
    }

    // Deletion test
    @Test
    void shouldThrowTaskNotFoundExceptionWhenTaskDoesNotExistWhileDeletingTask() {
        // Setup
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Main test and Assert
        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(taskId));

        // Verifies that findById() was called and delete() wasn't
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).delete(any(Task.class));
    }

    // Update PUT test
    @Test
    void shouldUpdateAndSaveTaskSuccessfully() {
        // Setup
        Long taskId = 1L;
        Task task = new Task("Original title", "Original description", TaskStatus.PENDING, null);
        PutTaskRequestDTO taskUpdate = new PutTaskRequestDTO("Updated title", "Updated description", TaskStatus.COMPLETED);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));
        when(taskRepository.save(task))
                .thenReturn(task);

        // Main Test
        Task updatedTask = assertDoesNotThrow(() -> taskService.updatePutTask(taskId, taskUpdate));

        // Assert results
        assertEquals("Updated title", updatedTask.getTitle());
        assertEquals("Updated description", updatedTask.getDescription());
        assertEquals(TaskStatus.COMPLETED, updatedTask.getTaskStatus());

        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(task);
    }

    // Update PUT test
    @Test
    void shouldThrowTaskNotFoundExceptionWhenTaskDoesNotExistWhileUpdatingPutTask() {
        // Setup
        Long taskId = 1L;
        PutTaskRequestDTO taskUpdate = new PutTaskRequestDTO("Updated title", "Updated description", TaskStatus.PENDING);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.empty());

        // Main Test and Assert
        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updatePutTask(taskId, taskUpdate)
        );
        assertEquals("task was not found", exception.getMessage());

        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    // Update PUT test
    @Test
    void shouldThrowInvalidTaskExceptionWhenTitleAndDescriptionAreBlank() {
        // Setup
        Long taskId = 1L;
        Task task = new Task("Original title", "Original description", TaskStatus.PENDING, null);
        PutTaskRequestDTO taskUpdate = new PutTaskRequestDTO(" ", " ", TaskStatus.PENDING);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        // Main Test
        InvalidTaskException exception = assertThrows(
                InvalidTaskException.class,
                () -> taskService.updatePutTask(taskId, taskUpdate)
        );

        // Assert
        assertEquals("either title or description must have text", exception.getMessage());

        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    // Update PUT test
    @Test
    void shouldThrowInvalidTaskExceptionWhenTitleOrDescriptionIsNull() {
        // Setup
        Long taskId = 1L;
        Task task = new Task("Original title", "Original description", TaskStatus.PENDING, null);
        PutTaskRequestDTO taskUpdate = new PutTaskRequestDTO(null, "Updated description", TaskStatus.PENDING);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        // Main Test and Assert
        InvalidTaskException exception = assertThrows(
                InvalidTaskException.class,
                () -> taskService.updatePutTask(taskId, taskUpdate)
        );
        assertEquals("title and description must be provided", exception.getMessage());

        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    // Update PATCH test
    @Test
    void shouldUpdateTaskSuccessfullyWithTitleAndDescription() {
        // Setup
        Long taskId = 1L;
        Task task = new Task("Original title", "Original description", TaskStatus.PENDING, null);
        PatchTaskRequestDTO taskUpdate = new PatchTaskRequestDTO("Updated title", "Updated description", null);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));
        when(taskRepository.save(task))
                .thenReturn(task);

        // Main Test
        Task updatedTask = assertDoesNotThrow(() -> taskService.updatePatchTask(taskId, taskUpdate));

        // Assert results
        assertEquals("Updated title", updatedTask.getTitle());
        assertEquals("Updated description", updatedTask.getDescription());
        assertEquals(TaskStatus.PENDING, updatedTask.getTaskStatus());

        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(task);
    }

    // Update PATCH test
    @Test
    void shouldThrowTaskNotFoundExceptionWhenTaskDoesNotExistWhileUpdatePatchTask() {
        // Setup
        Long taskId = 1L;
        PatchTaskRequestDTO taskUpdate = new PatchTaskRequestDTO("Updated title", "Updated description", TaskStatus.PENDING);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.empty());

        // Main Test
        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updatePatchTask(taskId, taskUpdate)
        );

        // Assert result
        assertEquals("task was not found", exception.getMessage());

        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    // Update PATCH test
    @Test
    void shouldThrowInvalidTaskExceptionWhenNoFieldIsProvidedWhileUpdatePatchTask() {
        // Setup
        Long taskId = 1L;
        Task task = new Task("Original title", "Original description", TaskStatus.PENDING, null);
        PatchTaskRequestDTO taskUpdate = new PatchTaskRequestDTO(null, null, null);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        // Main Test
        InvalidTaskException exception = assertThrows(
                InvalidTaskException.class,
                () -> taskService.updatePatchTask(taskId, taskUpdate)
        );

        // Assert results
        assertEquals("at least one field of the task must be provided", exception.getMessage());

        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    // Update PATCH test
    @Test
    void shouldUpdateTaskSuccessfullyWithTaskStatusWhileUpdatePatchTask() {
        // Setup
        Long taskId = 1L;
        Task task = new Task("Original title", "Original description", TaskStatus.PENDING, null);
        PatchTaskRequestDTO taskUpdate = new PatchTaskRequestDTO(null, null, TaskStatus.COMPLETED);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));
        when(taskRepository.save(task))
                .thenReturn(task);

        // Main Test
        Task updatedTask = assertDoesNotThrow(() -> taskService.updatePatchTask(taskId, taskUpdate));

        // Assert results
        assertEquals("Original title", updatedTask.getTitle());
        assertEquals("Original description", updatedTask.getDescription());
        assertEquals(TaskStatus.COMPLETED, updatedTask.getTaskStatus());

        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(task);
    }



}