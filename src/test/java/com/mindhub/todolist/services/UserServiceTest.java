package com.mindhub.todolist.services;

import com.mindhub.todolist.dtos.user.NewUserRequestDTO;
import com.mindhub.todolist.models.UserAuthority;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.UserRepository;
import com.mindhub.todolist.services.implementations.UserServiceImp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    UserServiceImp userService;

    @MockitoBean
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void shouldCreateUserSuccessfully() {
        // Setup
        NewUserRequestDTO newUserRequestDTO = new NewUserRequestDTO("username", "test@email.com", "password123", null);
        UserEntity user = new UserEntity(newUserRequestDTO);
        user.setPassword(passwordEncoder.encode(newUserRequestDTO.password()));

        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(user);

        // Main Test
        UserEntity createdUser = assertDoesNotThrow(() -> userService.createUser(null, newUserRequestDTO));

        // Assert results
        assertNotNull(createdUser);
        assertEquals("username", createdUser.getUsername());
        assertEquals("test@email.com", createdUser.getEmail());
        assertEquals(user.getPassword(), createdUser.getPassword());
        assertEquals(UserAuthority.USER, createdUser.getAuthority());
    }

    @Test
    void shouldCreateUserSuccessfullyWhenLoggedInUserHasAdminRole() {
        // Setup
        NewUserRequestDTO newUserRequestDTO = new NewUserRequestDTO("username", "test@email.com", "password123", UserAuthority.ADMIN);
        UserEntity user = new UserEntity(newUserRequestDTO);
        user.setAuthority(UserAuthority.ADMIN);
        user.setPassword(passwordEncoder.encode(newUserRequestDTO.password()));
        String admin = "admin@email.com";

        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(user);
        when(userRepository.existsByEmailAndAuthority(admin, UserAuthority.ADMIN))
                .thenReturn(true);

        // Main Test
        UserEntity createdUser = assertDoesNotThrow(() -> userService.createUser(admin, newUserRequestDTO));

        // Assert results
        assertEquals("username", createdUser.getUsername());
        assertEquals("test@email.com", createdUser.getEmail());
        assertEquals(UserAuthority.ADMIN, createdUser.getAuthority());
        assertEquals(user.getPassword(), createdUser.getPassword());
    }






}