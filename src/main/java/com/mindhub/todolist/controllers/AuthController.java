package com.mindhub.todolist.controllers;

import com.mindhub.todolist.config.JwtUtils;
import com.mindhub.todolist.dtos.user.LoginUserRequestDTO;
import com.mindhub.todolist.dtos.user.NewUserRequestDTO;
import com.mindhub.todolist.exceptions.InvalidUserException;
import com.mindhub.todolist.models.UserAuthority;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;


    @Operation(
            summary = "Authentication of a user or admin.",
            description = "Get authenticated by submitting email and password. Returns JWT Token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful. A JWT token is returned."),
                    @ApiResponse(responseCode =  "400", description = "The provided email or password are incorrect.")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Email and password must be provided",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginUserRequestDTO.class),
                            examples = @ExampleObject(value = "{\"email\": \"email@email.com\", \"password\": \"password123\"}")
                    )
            )
    )
    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@Valid @RequestBody LoginUserRequestDTO loginRequest, boolean isCreated) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(), loginRequest.password()
                ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(authentication.getName());
        return isCreated ? ResponseEntity.status(HttpStatus.CREATED).body(jwt) : ResponseEntity.ok(jwt);
    }

    @Operation(
            summary = "Registration of a user or admin",
            description = "Create a new user or admin (if an admin is logged in)",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Registration successful. A JWT token is returned."),
                    @ApiResponse(responseCode =  "400", description = "Email or password are invalid.")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Body of the registration application. Username, email, password and authority (if an admin is logged in, otherwise this will be ignored and set to a user).",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginUserRequestDTO.class),
                            examples = @ExampleObject(value = "{\"username\": \"username\",\"email\": \"user@email.com\", \"password\": \"password123\"}")
                    )
            )
    )
    @PostMapping("/register")
    public ResponseEntity<String> registerNewUser(Authentication authentication, @Valid @RequestBody NewUserRequestDTO registerRequest) {
        UserEntity user = userService.createUser(authentication, registerRequest);
        return authenticateUser(new LoginUserRequestDTO(user.getEmail(), registerRequest.password()), true);
    }

}


