package com.mindhub.todolist.controllers;

import com.mindhub.todolist.dtos.user.*;
import com.mindhub.todolist.exceptions.EmailAlreadyExistsException;
import com.mindhub.todolist.exceptions.InvalidUserException;
import com.mindhub.todolist.exceptions.UnauthorizedException;
import com.mindhub.todolist.exceptions.UserNotFoundException;
import com.mindhub.todolist.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(
            summary = "Returns all users. (admin auth is required)",
            description = "Retrieves all users.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users successfully returned."),
                    @ApiResponse(responseCode = "403", description = "Access denied.")
            }
    )
    @GetMapping
    public ResponseEntity<List<ExtendedUserDTO>> getAllUsers() {
        return userService.getAllUsersRequest();
    }

    @Operation(
            summary = "Returns a single user. (admin auth is required)",
            description = "Returns a single user by id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully returned."),
                    @ApiResponse(responseCode = "403", description = "You don't have access to the resources.")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ExtendedUserDTO> getUserById(@PathVariable Long id) throws UserNotFoundException {
        return userService.getUserByIdRequest(id);
    }

    @Operation(
            summary = "Returns the logged in user. (user or admin auth is required)",
            description = "Returns the user that the JWT token has.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully returned."),
                    @ApiResponse(responseCode = "403", description = "The JWT is invalid.")
            }
    )
    @GetMapping("/self")
    public ResponseEntity<UserDTO> getUser(Authentication authentication) throws UserNotFoundException {
        return userService.getUserByEmailRequest(authentication.getName());
    }

    @Operation(
            summary = "Deletes user by ID. (admin auth is required)",
            description = "Deletes user. Returns confirmation code.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User successfully deleted."),
                    @ApiResponse(responseCode = "403", description = "The JWT is invalid.")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(Authentication authentication, @PathVariable Long id) throws UserNotFoundException, UnauthorizedException {
        return userService.deleteUserByIdRequest(authentication, id);
    }

    @Operation(
            summary = "Deletes logged in user. (user or admin auth is required)",
            description = "Deletes user. Returns confirmation code.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User successfully deleted."),
                    @ApiResponse(responseCode = "403", description = "Access denied.")
            }
    )
    @DeleteMapping("/self")
    public ResponseEntity<?> deleteUser(Authentication authentication) throws UserNotFoundException, UnauthorizedException {
        return userService.deleteUserRequest(authentication.getName());
    }

    @Operation(
            summary = "Updates all attributes of user by ID. (admin auth is required)",
            description = "Sets the user with the one provided. Returns the user updated.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully updated."),
                    @ApiResponse(responseCode = "400", description = "Invalid user provided."),
                    @ApiResponse(responseCode = "403", description = "Access denied."),
                    @ApiResponse(responseCode = "404", description = "User not found."),
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updatePutUser(@PathVariable Long id, @Valid @RequestBody PutUserRequestDTO putUserRequestDTO) throws UserNotFoundException, EmailAlreadyExistsException, InvalidUserException {
        return userService.updatePutUserByIdRequest(id, putUserRequestDTO);
    }

    @Operation(
            summary = "Updates all attributes of logged in user. (user or admin auth is required)",
            description = "Sets the user with the one provided. Returns the user updated.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully updated."),
                    @ApiResponse(responseCode = "400", description = "Invalid user provided."),
                    @ApiResponse(responseCode = "403", description = "Access denied.")
            }
    )
    @PutMapping("/self")
    public ResponseEntity<UserDTO> updatePutUser(Authentication authentication, @Valid @RequestBody PutUserRequestDTO putUserRequestDTO) throws UserNotFoundException, EmailAlreadyExistsException, InvalidUserException {
        return userService.updatePutUserRequest(authentication.getName(), putUserRequestDTO);
    }

    @Operation(
            summary = "Updates provided attributes of user by ID. (admin auth is required)",
            description = "Updates user with the attributes provided. Returns the user updated.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully updated."),
                    @ApiResponse(responseCode = "400", description = "Invalid user provided."),
                    @ApiResponse(responseCode = "403", description = "Access denied.")
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<ExtendedUserDTO> updatePatchUser(@PathVariable Long id, @Valid @RequestBody PatchUserRequestDTO patchUserRequestDTO) throws UserNotFoundException, InvalidUserException {
        return userService.updatePatchUserByIdRequest(id, patchUserRequestDTO);
    }

    @Operation(
            summary = "Updates provided attributes of logged  in user. (user or admin auth is required)",
            description = "Updates user with the attributes provided. Returns the user updated.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully updated."),
                    @ApiResponse(responseCode = "400", description = "Invalid user provided."),
                    @ApiResponse(responseCode = "403", description = "Access denied.")
            }
    )
    @PatchMapping("/self")
    public ResponseEntity<UserDTO> updatePatchUser(Authentication authentication, @Valid @RequestBody PatchUserRequestDTO patchUserRequestDTO) throws UserNotFoundException, InvalidUserException {
        return userService.updatePatchUserRequest(authentication.getName(), patchUserRequestDTO);
    }
}
