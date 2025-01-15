package com.mindhub.todolist.controllers;

import com.mindhub.todolist.dtos.user.NewUserRequestDTO;
import com.mindhub.todolist.dtos.user.PatchUserRequestDTO;
import com.mindhub.todolist.dtos.user.PutUserRequestDTO;
import com.mindhub.todolist.dtos.user.UserDTO;
import com.mindhub.todolist.exceptions.EmailAlreadyExistsException;
import com.mindhub.todolist.exceptions.InvalidUserException;
import com.mindhub.todolist.exceptions.UnauthorizedException;
import com.mindhub.todolist.exceptions.UserNotFoundException;
import com.mindhub.todolist.services.UserService;
import com.mindhub.todolist.services.implementations.UserServiceImp;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@Controller
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return userService.getAllUsersRequest();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) throws UserNotFoundException {
        return userService.getUserByIdRequest(id);
    }

    @GetMapping("/self")
    public ResponseEntity<UserDTO> getUser(Authentication authentication) throws UserNotFoundException {
        return userService.getUserByEmailRequest(authentication.getName());
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(Authentication authentication, @Valid @RequestBody NewUserRequestDTO newUserRequestDTO) {
        return userService.createUserRequest(authentication, newUserRequestDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(Authentication authentication, @PathVariable Long id) throws UserNotFoundException, UnauthorizedException {
        return userService.deleteUserRequest(authentication, id);
    }

    @DeleteMapping("/self")
    public ResponseEntity<?> deleteUser(Authentication authentication) throws UserNotFoundException, UnauthorizedException {
        return userService.deleteUserRequest(authentication.getName());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updatePutUser(@PathVariable Long id, @Valid @RequestBody PutUserRequestDTO putUserRequestDTO) throws UserNotFoundException, EmailAlreadyExistsException, InvalidUserException {
        return userService.updatePutUserByIdRequest(id, putUserRequestDTO);
    }

    @PutMapping("/self")
    public ResponseEntity<UserDTO> updatePutUser(Authentication authentication, @Valid @RequestBody PutUserRequestDTO putUserRequestDTO) throws UserNotFoundException, EmailAlreadyExistsException, InvalidUserException {
        return userService.updatePutUserRequest(authentication.getName(), putUserRequestDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> updatePatchUser(@PathVariable Long id, @Valid @RequestBody PatchUserRequestDTO patchUserRequestDTO) throws UserNotFoundException, InvalidUserException {
        return userService.updatePatchUserByIdRequest(id, patchUserRequestDTO);
    }

    @PatchMapping("/self")
    public ResponseEntity<UserDTO> updatePatchUser(Authentication authentication, @Valid @RequestBody PatchUserRequestDTO patchUserRequestDTO) throws UserNotFoundException, InvalidUserException {
        return userService.updatePatchUserRequest(authentication.getName(), patchUserRequestDTO);
    }
}
