package com.mindhub.todolist.dtos.user;

import com.mindhub.todolist.models.UserAuthority;

public record PatchUserRequestDTO(
        String username,
        String email,
        String password,
        UserAuthority authority
) {
}
