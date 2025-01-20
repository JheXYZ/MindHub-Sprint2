package com.mindhub.todolist.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindhub.todolist.dtos.user.PutUserRequestDTO;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

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
    public void init() {
        userRepository.saveAll(
                List.of(
                        new UserEntity("user1@email.com", passwordEncoder.encode("password123"), "User1", UserAuthority.USER),
                        new UserEntity("user2@email.com", passwordEncoder.encode("password123"), "User2", UserAuthority.USER),
                        new UserEntity("user3@email.com", passwordEncoder.encode("password123"), "User3", UserAuthority.USER),
                        new UserEntity("user4@email.com", passwordEncoder.encode("password123"), "User4", UserAuthority.USER),
                        new UserEntity("admin1@email.com", passwordEncoder.encode("password123"), "Admin1", UserAuthority.ADMIN)
                )
        );
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin1@email.com", authorities = "ADMIN")
    public void shouldReturnAllUsersOnlyIfRequestIsFromAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].authority").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].tasks").exists());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin1@email.com", authorities = "ADMIN")
    public void shouldReturnSingleUserOnlyIfRequestIsFromAdmin() throws Exception {
        long userId = 1L;
        mockMvc.perform(get("/api/v1/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("user1@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("User1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authority").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authority").value("USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tasks").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "user1@email.com", authorities = "USER")
    public void shouldReturnUserFromLoggedInUser() throws Exception {
        long expectedId = 1L;

        mockMvc.perform(get("/api/v1/users/self"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(expectedId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("user1@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("User1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tasks").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authority").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin1@email.com", authorities = "ADMIN")
    public void shouldDeleteUserByIdOnlyIfRequestIsFromAdmin() throws Exception {
        long userId = 1L;

        assertNotNull(userRepository.findById(userId).orElse(null));

        mockMvc.perform(delete("/api/v1/users/" + userId))
                .andExpect(status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());

        assertNull(userRepository.findById(userId).orElse(null));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "user1@email.com", authorities = "USER")
    public void shouldDeleteLoggedUser() throws Exception {
        long userId = 1L;

        assertNotNull(userRepository.findById(userId).orElse(null));

        mockMvc.perform(delete("/api/v1/users/self"))
                .andExpect(status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist());

        assertNull(userRepository.findById(userId).orElse(null));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin1@email.com", authorities = "ADMIN")
    public void shouldUpdatePutUserOnlyIfRequestIsFromAdminAndReturnIt() throws Exception {
        long userId = 1L;

        PutUserRequestDTO putUser = new PutUserRequestDTO("UpdatePutUser1", "updatePutUser1@email.com", "newPassword123", UserAuthority.ADMIN);

        mockMvc.perform(
                put("/api/v1/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(putUser)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("updatePutUser1@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("UpdatePutUser1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tasks").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authority").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authority").value("ADMIN"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "user1@email.com", authorities = "USER")
    public void shouldUpdatePutUserFromLoggedInUserAndReturnIt() throws Exception {
        long userId = 1L;

        PutUserRequestDTO putUser = new PutUserRequestDTO("UpdatePatchUser1", "updatePatchUser1@email.com", "newPassword123", null);

        mockMvc.perform(
                put("/api/v1/users/self")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(putUser)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("updatePatchUser1@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("UpdatePatchUser1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tasks").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authority").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin1@email.com", authorities = "ADMIN")
    public void shouldUpdatePatchUserOnlyIfRequestIsFromAdminAndReturnIt() throws Exception {
        long userId = 1L;

        PutUserRequestDTO putUser = new PutUserRequestDTO("UpdatePutUser1", "updatePutUser1@email.com", "newPassword123", UserAuthority.ADMIN);

        mockMvc.perform(
                        patch("/api/v1/users/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(putUser)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("updatePutUser1@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("UpdatePutUser1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tasks").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authority").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authority").value("ADMIN"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist());

    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "user1@email.com", authorities = "USER")
    public void shouldUpdatePatchUserFromLoggedInUserAndReturnIt() throws Exception {
        long userId = 1L;

        PutUserRequestDTO putUser = new PutUserRequestDTO("UpdatePatchUser1", "updatePatchUser1@email.com", "newPassword123", null);

        mockMvc.perform(
                        patch("/api/v1/users/self")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(putUser)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("updatePatchUser1@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("UpdatePatchUser1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tasks").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authority").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist());
    }



}