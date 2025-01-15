package com.mindhub.todolist.dtos.user;

import com.mindhub.todolist.models.UserAuthority;
import com.mindhub.todolist.validations.ValidEmail;

public record PatchUserRequestDTO(
        String username,
        @ValidEmail
        String email,
        String password,
        UserAuthority authority
) {
}
