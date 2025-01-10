package com.mindhub.todolist.dtos.user;

import com.mindhub.todolist.validations.NoWhitespaces;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;


public record PutUserRequestDTO(
        @NotBlank(message = "username must not be empty")
        @NoWhitespaces(message = "username can not contain whitespaces")
        String username,
        @NotBlank(message = "email must not be empty")
        @NoWhitespaces(message = "username can not contain whitespaces")
        @Email(message = "invalid email")
        String email,
        @NotBlank(message = "password must not be empty")
        @NoWhitespaces(message = "username can not contain whitespaces")
        @Length(min = 6, message = "password must have at least 6 characters")
        String password
) {}