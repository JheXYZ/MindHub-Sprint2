package com.mindhub.todolist.dtos.user;

import com.mindhub.todolist.models.UserAuthority;
import com.mindhub.todolist.validations.NoWhitespaces;
import com.mindhub.todolist.validations.UniqueEmail;
import com.mindhub.todolist.validations.ValidEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record NewUserRequestDTO(
        @NotBlank(message = "username must not be empty")
        @NoWhitespaces(message = "username can not contain whitespaces")
        String username,
        @ValidEmail
        @UniqueEmail
        String email,
        @NotBlank(message = "password must not be empty")
        @NoWhitespaces(message = "password can not contain whitespaces")
        @Length(min = 6, max = 40, message = "password must have between 6 and 40 characters")
        String password,
        UserAuthority authority
) {
}