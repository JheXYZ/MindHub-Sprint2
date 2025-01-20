package com.mindhub.todolist.repositories;

import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.TaskStatus;
import com.mindhub.todolist.models.UserAuthority;
import com.mindhub.todolist.models.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity user;

    private Task task1, task2;

    @BeforeEach
    public void init(){
        user = userRepository.save(new UserEntity("user1@email.com", "password123", "User1", UserAuthority.USER));
        task1 = new Task("Title of task 1", "Description of task 1", TaskStatus.IN_PROGRESS, user);
        task2 = new Task("Title of task 2", "Description of task 2", TaskStatus.PENDING, user);
        taskRepository.save(task1);
        taskRepository.save(task2);
    }

    @Test
    @DirtiesContext
    void shouldReturnListOfTasksByIdOfUser() {
        long userId = 1L;
        List<Task> taskRes = taskRepository.findByUser_id(userId);
        assertNotNull(taskRes);
        assertEquals(taskRes.size(), 2);
        assertTrue(taskRes.containsAll(List.of(task1, task2)));
    }

    @Test
    @DirtiesContext
    void shouldNotFindTasksOfNoneExistingUserById() {
        long userId = 2L;
        assertFalse(userRepository.existsById(userId));
        assertEquals(taskRepository.findByUser_id(userId).size(), 0);
    }

    @Test
    @DirtiesContext
    void shouldReturnListOfTasksByEmailOfUser() {
        String email = "user1@email.com";
        List<Task> taskRes = taskRepository.findByUser_email(email);
        assertNotNull(taskRes);
        assertEquals(taskRes.size(), 2);
        assertTrue(taskRes.containsAll(List.of(task1, task2)));
    }

    @Test
    @DirtiesContext
    void shouldNotFindTasksOfNoneExistingUserByEmail() {
        String email = "user2@email.com";
        assertFalse(userRepository.existsByEmail(email));
        assertEquals(taskRepository.findByUser_email(email).size(), 0);
    }

    @Test
    @DirtiesContext
    void shouldReturnTrueIfUserHasTaskById() {
        long taskId = 1L;
        String email = "user1@email.com";
        assertTrue(taskRepository.existsByIdAndUser_email(taskId, email));
    }

    @Test
    @DirtiesContext
    void shouldNotFindTaskOfUserByEmailAndTaskId() {
        long taskId = 3L;
        String email = "user1@email.com";
        assertFalse(taskRepository.existsByIdAndUser_email(taskId, email));
    }
}