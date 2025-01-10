package com.mindhub.todolist.dtos.user;

import com.mindhub.todolist.validations.NoWhitespaces;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserTaskRequestDTO(

        @NotNull(message = "email must be provided")
        @Email(message = "invalid email")
        String email,
        @NotBlank(message = "password must not be empty")
        @NoWhitespaces(message = "password can not contain whitespaces")
        String password
) {
}
