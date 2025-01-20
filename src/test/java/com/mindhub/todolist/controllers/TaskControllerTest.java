package com.mindhub.todolist.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindhub.todolist.dtos.task.NewTaskRequestDTO;
import com.mindhub.todolist.dtos.task.PutTaskRequestDTO;
import com.mindhub.todolist.exceptions.CustomExceptionsHandler.ErrorResponse;
import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.TaskStatus;
import com.mindhub.todolist.models.UserAuthority;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.TaskRepository;
import com.mindhub.todolist.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void init(){
        userRepository.saveAll(
            List.of(
                    new UserEntity("user1@email.com", passwordEncoder.encode("password123"), "User1", UserAuthority.USER),
                    new UserEntity("user2@email.com", passwordEncoder.encode("password123"), "User2", UserAuthority.USER),
                    new UserEntity("user3@email.com", passwordEncoder.encode("password123"), "User3", UserAuthority.USER),
                    new UserEntity("user4@email.com", passwordEncoder.encode("password123"), "User4", UserAuthority.USER),
                    new UserEntity("admin1@email.com", passwordEncoder.encode("password123"), "Admin1", UserAuthority.ADMIN)
            )
        );
        taskRepository.saveAll(
                List.of(
                        new Task("Title of task 1", "Description for task 1", TaskStatus.PENDING, userRepository.findById(1L).orElse(null)),
                        new Task("Title of task 2", "Description for task 2", TaskStatus.PENDING, userRepository.findById(1L).orElse(null)),
                        new Task("Title of task 3", "Description for task 3", TaskStatus.PENDING, userRepository.findById(2L).orElse(null)),
                        new Task("Title of task 4", "Description for task 4", TaskStatus.PENDING, userRepository.findById(2L).orElse(null)),
                        new Task("Title of task 5", "Description for task 5", TaskStatus.PENDING, userRepository.findById(2L).orElse(null)),
                        new Task("Title of task 6", "Description for task 6", TaskStatus.PENDING, userRepository.findById(3L).orElse(null)),
                        new Task("Title of task 7", "Description for task 7", TaskStatus.PENDING, userRepository.findById(3L).orElse(null))
                )
        );
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin1@email.com", authorities = "ADMIN")
    public void shouldReturnAllTasksOnlyIfRequestIsFromAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(7))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].taskStatus").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].user").exists());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "user1@email.com", authorities = "USER")
    public void shouldReturnForbiddenWhenLoggedInUserIsNotAnAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user2@email.com", authorities = "USER")
    @DirtiesContext
    public void shouldReturnTasksFromLoggedInUser() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/user"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].taskStatus").exists());
    }

    @Test
    @DirtiesContext
    public void shouldReturnForbiddenWhenNoUserIsLoggedIn() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "user3@email.com", authorities = "USER")
    public void shouldGetSpecificTaskFromLoggedInUser() throws Exception {
        long taskId = 6L;

        mockMvc.perform(get("/api/v1/tasks/user/" + taskId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(taskId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Title of task 6"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Description for task 6"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskStatus").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskStatus").value("PENDING"));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "user3@email.com", authorities = "USER")
    public void shouldReturnNotFoundWhenLoggedInUserTriesToGetNoneExistingTaskOrDoesNotOwnSaidTaskById() throws Exception {
        long taskId = 1L;

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/tasks/user/" + taskId))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.length()").value(1))
                .andReturn();

        List<String> errorsResponse = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class)
                .errors();
        assertEquals("task was not found", errorsResponse.get(0));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin1@email.com", authorities = "ADMIN")
    public void shouldCreateANewTaskToAUserOnlyIfRequestIsFromAdmin() throws Exception {
        NewTaskRequestDTO task = new NewTaskRequestDTO("new Task", "task description", TaskStatus.IN_PROGRESS);
        long userId = 4L, expectedTaskId = 8L;

        mockMvc.perform(
                post("/api/v1/tasks/user/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(expectedTaskId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("new Task"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("task description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskStatus").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskStatus").value("IN_PROGRESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user").exists());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "user1@email.com", authorities = "USER")
    public void shouldReturnForbiddenWhenLoggedInUserIsNotAnAdminAndTriesToCreateATaskToAnotherUser() throws Exception {
        long userId = 2L;

        mockMvc.perform(
                post("/api/v1/tasks/user/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewTaskRequestDTO(null, null, null))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "user4@email.com", authorities = "USER")
    public void shouldCreateANewTaskToLoggedInUser() throws Exception {
        NewTaskRequestDTO task = new NewTaskRequestDTO("new Task to user4@email.com", "task description for user4", TaskStatus.COMPLETED);
        long expectedTaskId = 8L;

        mockMvc.perform(
                post("/api/v1/tasks/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(expectedTaskId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("new Task to user4@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("task description for user4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskStatus").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskStatus").value("COMPLETED"));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "user4@email.com", authorities = "USER")
    public void shouldReturnBadRequestWhenLoggedInUserTriesToCreateAnEmptyTask() throws Exception {
        NewTaskRequestDTO task = new NewTaskRequestDTO("", "", TaskStatus.COMPLETED);

        MvcResult mvcResult = mockMvc.perform(
                post("/api/v1/tasks/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.length()").value(1))
                .andReturn();

        List<String> errorsResponse = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class)
                .errors();
        assertEquals("either title or description must have text", errorsResponse.get(0));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "user4@email.com", authorities = "USER")
    public void shouldReturnBadRequestWhenLoggedInUserTriesToCreateAnInvalidTask() throws Exception {
        NewTaskRequestDTO task = new NewTaskRequestDTO(null, null, TaskStatus.COMPLETED);

        MvcResult mvcResult = mockMvc.perform(
                post("/api/v1/tasks/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.length()").value(1))
                .andReturn();

        List<String> errorsResponse = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class)
                .errors();
        assertEquals("title and description must be provided", errorsResponse.get(0));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "user1@email.com", authorities = "USER")
    public void shouldDeleteTaskFromLoggedInUser() throws Exception {
        long taskId = 1L;
        // asserts that task exists prior to deletion
        assertNotNull(taskRepository.findById(taskId));

        mockMvc.perform(delete("/api/v1/tasks/user/" + taskId))
                .andExpect(status().isNoContent());

        // asserts that task doesn't exists
        assertNull(taskRepository.findById(taskId).orElse(null));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "user1@email.com", authorities = "USER")
    public void shouldReturnNotFoundWhenLoggedInUserTriesToDeleteANoneExistingTaskOrDoesNotOwnIt() throws Exception {
        long taskId = 3L;

        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/tasks/user/" + taskId))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.length()").value(1))
                .andReturn();

        List<String> errorsResponse = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class)
                .errors();
        assertEquals("task was not found", errorsResponse.get(0));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin1@email.com", authorities = "ADMIN")
    public void shouldDeleteTaskByIdOnlyIfRequestIsFromAdmin() throws Exception {
        long taskId = 1L;
        // asserts that task exists prior to deletion
        assertNotNull(taskRepository.findById(taskId));

        mockMvc.perform(delete("/api/v1/tasks/" + taskId))
                .andExpect(status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());

        // asserts that task doesn't exists
        assertNull(taskRepository.findById(taskId).orElse(null));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "user3@email.com", authorities = "USER")
    public void shouldUpdatePutTaskFromLoggedInUser() throws Exception {
        long taskId = 7L;
        PutTaskRequestDTO updateTask = new PutTaskRequestDTO("Updated put title task 7", "Updated put description task 7", TaskStatus.COMPLETED);

        mockMvc.perform(
                put("/api/v1/tasks/user/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTask)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(taskId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated put title task 7"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated put description task 7"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskStatus").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskStatus").value("COMPLETED"));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin1@email.com", authorities = "ADMIN")
    public void shouldUpdatePutTaskFromUserByIdOnlyIfRequestIsFromAdmin() throws Exception {
        long taskId = 7L;
        PutTaskRequestDTO updateTask = new PutTaskRequestDTO("Updated put title with admin auth task 7", "Updated put description with admin auth task 7", TaskStatus.COMPLETED);

        mockMvc.perform(
                        put("/api/v1/tasks/" + taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateTask)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(taskId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated put title with admin auth task 7"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated put description with admin auth task 7"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskStatus").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskStatus").value("COMPLETED"));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "user3@email.com", authorities = "USER")
    public void shouldUpdatePatchTaskFromLoggedInUser() throws Exception {
        long taskId = 7L;
        PutTaskRequestDTO updateTask = new PutTaskRequestDTO("Updated patch title task 7", "Updated patch description task 7", TaskStatus.COMPLETED);

        mockMvc.perform(
                        patch("/api/v1/tasks/user/" + taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateTask)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(taskId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated patch title task 7"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated patch description task 7"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskStatus").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskStatus").value("COMPLETED"));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin1@email.com", authorities = "ADMIN")
    public void shouldUpdatePatchTaskFromUserByIdOnlyIfRequestIsFromAdmin() throws Exception {
        long taskId = 7L;
        PutTaskRequestDTO updateTask = new PutTaskRequestDTO("Updated patch title with admin auth task 7", "Updated patch description with admin auth task 7", TaskStatus.COMPLETED);

        mockMvc.perform(
                        patch("/api/v1/tasks/" + taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateTask)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(taskId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated patch title with admin auth task 7"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated patch description with admin auth task 7"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskStatus").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskStatus").value("COMPLETED"));
    }







}