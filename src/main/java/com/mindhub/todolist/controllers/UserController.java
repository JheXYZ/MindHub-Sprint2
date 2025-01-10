package com.mindhub.todolist.controllers;

import com.mindhub.todolist.dtos.user.NewUserRequestDTO;
import com.mindhub.todolist.dtos.user.PatchUserRequestDTO;
import com.mindhub.todolist.dtos.user.PutUserRequestDTO;
import com.mindhub.todolist.dtos.user.UserDTO;
import com.mindhub.todolist.exceptions.EmailAlreadyExistsException;
import com.mindhub.todolist.exceptions.InvalidUserException;
import com.mindhub.todolist.exceptions.UserNotFoundException;
import com.mindhub.todolist.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@Controller
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return userService.getAllUsersDTO();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) throws UserNotFoundException {
        return userService.getUserDTOById(id);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody NewUserRequestDTO newUserRequestDTO) {
        return userService.createUser(newUserRequestDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) throws UserNotFoundException {
        return userService.deleteUser(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updatePutUser(@PathVariable Long id, @Valid @RequestBody PutUserRequestDTO putUserRequestDTO) throws UserNotFoundException, EmailAlreadyExistsException {
        return userService.updatePutUser(id, putUserRequestDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> updatePatchUser(@PathVariable Long id, @Valid @RequestBody PatchUserRequestDTO patchUserRequestDTO) throws UserNotFoundException, InvalidUserException {
        return userService.updatePatchUser(id, patchUserRequestDTO);
    }
}
