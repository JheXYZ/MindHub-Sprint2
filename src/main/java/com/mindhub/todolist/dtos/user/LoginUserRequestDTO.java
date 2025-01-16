package com.mindhub.todolist.dtos.user;

import com.mindhub.todolist.validations.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginUserRequestDTO(
        @NotNull(message = "email must be provided") @ValidEmail String email,
        @NotBlank(message = "password must be provided") String password)
{}
