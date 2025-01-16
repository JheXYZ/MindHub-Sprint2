package com.mindhub.todolist.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindhub.todolist.config.JwtUtils;
import com.mindhub.todolist.config.SecurityConfig;
import com.mindhub.todolist.dtos.task.TaskDTO;
import com.mindhub.todolist.dtos.user.NewUserRequestDTO;
import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.TaskStatus;
import com.mindhub.todolist.models.UserAuthority;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.TaskRepository;
import com.mindhub.todolist.repositories.UserRepository;
import com.mindhub.todolist.services.TaskService;
import com.mindhub.todolist.services.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



/*@AutoConfigureMockMvc*/
@WebMvcTest(TaskController.class)
/*@ComponentScan(basePackages = {"com.mindhub.todolist.services", "com.mindhub.todolist.repositories", "com.mindhub.todolist.config"})*/
/*@Import(SecurityConfig.class)*/
/*@ActiveProfiles("test")*/
/*@SpringBootTest*/
public class TaskControllerTest {

    @Test
    public void test(){

    }

    /*@Autowired
    private MockMvc mockTaskController;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskService taskService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    *//*@Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtils jwtUtils;*//*

    *//*@Autowired
    PasswordEncoder passwordEncoder;*//*

    private UserEntity normalUser;

    private UserEntity adminUser;

    private List<Task> tasks;


    @BeforeEach
    public void init(){
        normalUser = new UserEntity("user@email.com", "password123", "username", UserAuthority.USER);
        adminUser = new UserEntity("admin@email.com", "password123", "admin", UserAuthority.ADMIN);

        Task task1 = new Task("title1", "description1", TaskStatus.PENDING, normalUser);
        Task task2 = new Task("title2", "description2", TaskStatus.IN_PROGRESS, normalUser);
        Task task3 = new Task("title3", "description3", TaskStatus.COMPLETED, adminUser);
        tasks = new ArrayList<>(List.of(task1, task2, task3));
    }

    @Test
    @WithMockUser(username = "admin@email.com", authorities = {"ADMIN"})
    public void shouldReturnAllTasks() throws Exception {
        when(taskService.getAllTasksRequest())
                .thenReturn(ResponseEntity.ok().body(tasks.stream().map(TaskDTO::new).toList()));

        mockTaskController.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)));
    }*/

}