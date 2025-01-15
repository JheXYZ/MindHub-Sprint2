package com.mindhub.todolist.dtos.user;

import com.mindhub.todolist.validations.ValidEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginUserRequestDTO(
        @NotBlank(message = "email must be provided for authentication") @ValidEmail String email,
        @NotBlank(message = "password must be provided") String password)
{}
