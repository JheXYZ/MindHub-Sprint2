package com.mindhub.todolist.dtos.user;

public record PatchUserRequestDTO(
        String username,
        String email,
        String password) {
}
