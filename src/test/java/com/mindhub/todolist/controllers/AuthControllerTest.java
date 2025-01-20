package com.mindhub.todolist.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindhub.todolist.dtos.user.LoginUserRequestDTO;
import com.mindhub.todolist.dtos.user.NewUserRequestDTO;
import com.mindhub.todolist.exceptions.CustomExceptionsHandler.ErrorResponse;
import com.mindhub.todolist.models.UserAuthority;
import com.mindhub.todolist.models.UserEntity;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void initData() {
        userRepository.saveAll(
                List.of(
                        new UserEntity("user1@email.com", passwordEncoder.encode("password123"), "User1", UserAuthority.USER),
                        new UserEntity("admin1@email.com", passwordEncoder.encode("password123"), "Admin1", UserAuthority.ADMIN)
                )
        );
    }

    // Login test
    @Test
    @DirtiesContext
    public void shouldReturnJwtTokenFromLoginUserRequest() throws Exception {
        LoginUserRequestDTO loginAdmin = new LoginUserRequestDTO("user1@email.com", "password123");

        MvcResult mvcResult = mockMvc
                .perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginAdmin)))
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(mvcResult
                .getResponse()
                .getContentAsString()
                .matches("^[A-Za-z0-9-_]+(?:\\.[A-Za-z0-9-_]+){2}$") // checks that a valid JWT token is returned and not a random string
        );
    }

    // Login test
    @Test
    @DirtiesContext
    public void shouldReturnBadRequestWhenEmailAndPasswordAreBlank() throws Exception {
        LoginUserRequestDTO loginAdmin = new LoginUserRequestDTO("", "");

        MvcResult mvcResult = mockMvc
                .perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginAdmin)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.length()").value(2))
                .andReturn();

        List<String> errorsResponse = objectMapper
                .readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class)
                .errors();
        assertTrue(errorsResponse.containsAll(List.of("invalid email", "password must be provided")));
    }

    // Login test
    @Test
    @DirtiesContext
    public void shouldReturnBadRequestWhenEmailIsNotProvided() throws Exception {
        LoginUserRequestDTO loginAdmin = new LoginUserRequestDTO(null, "password123");

        MvcResult mvcResult = mockMvc
                .perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginAdmin)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.length()").value(1))
                .andReturn();

        List<String> errorsResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class).errors();
        assertEquals("email must be provided", errorsResponse.get(0));
    }

    // Login test
    @Test
    @DirtiesContext
    public void shouldReturnBadRequestWhenPasswordIsNotProvided() throws Exception {
        LoginUserRequestDTO loginAdmin = new LoginUserRequestDTO("user1@email.com", null);

        MvcResult mvcResult = mockMvc
                .perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginAdmin)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.length()").value(1))
                .andReturn();

        List<String> errorsResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class).errors();
        assertEquals("password must be provided", errorsResponse.get(0));
    }

    // Register test
    @Test
    @DirtiesContext
    public void shouldReturnJwtTokenFromRegisterNewUserRequest() throws Exception {
        NewUserRequestDTO newUserRequest = new NewUserRequestDTO("User2", "user2@email.com", "password123", null);

        MvcResult mvcResult = mockMvc
                .perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        assertTrue(mvcResult
                .getResponse()
                .getContentAsString()
                .matches("^[A-Za-z0-9-_]+(?:\\.[A-Za-z0-9-_]+){2}$") // checks that a valid JWT token is returned and not a random string
        );
    }

    // Register test
    @Test
    @DirtiesContext
    public void shouldReturnBadRequestWhenTriesToRegisterWithAlreadyTakenEmail() throws Exception {
        NewUserRequestDTO newUserRequest = new NewUserRequestDTO("User1", "user1@email.com", "password123", null);

        MvcResult mvcResult = mockMvc
                .perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.length()").value(1))
                .andReturn();

        List<String> errorsResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class).errors();
        assertEquals("email already taken", errorsResponse.get(0));
    }

    // Register test
    @Test
    @DirtiesContext
    @WithMockUser(username = "user1@email.com", authorities = "USER")
    public void shouldReturnUnauthorizedWhenLoggedInUserWithUSERAuthorityTriesToCreateAnAdmin() throws Exception {
        NewUserRequestDTO newUserRequest = new NewUserRequestDTO("User1", "admin2@email.com", "password123", UserAuthority.ADMIN);

        MvcResult mvcResult = mockMvc
                .perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.length()").value(1))
                .andReturn();

        List<String> errorsResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class).errors();
        assertEquals("unauthorized admin creation", errorsResponse.get(0));
    }

    // Register Test
    @Test
    @DirtiesContext
    @WithMockUser(username = "admin1@email.com", authorities = "ADMIN")
    public void shouldReturnJwtTokenFromRegisterNewAdminRequest() throws Exception {
        NewUserRequestDTO newUserRequest = new NewUserRequestDTO("Admin2", "admin2@email.com", "password123", UserAuthority.ADMIN);

        MvcResult mvcResult = mockMvc
                .perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        assertTrue(mvcResult
                .getResponse()
                .getContentAsString()
                .matches("^[A-Za-z0-9-_]+(?:\\.[A-Za-z0-9-_]+){2}$") // checks that a valid JWT token is returned and not a random string
        );
    }


}