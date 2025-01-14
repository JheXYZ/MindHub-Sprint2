package com.mindhub.todolist.services;

import com.mindhub.todolist.dtos.user.NewUserRequestDTO;
import com.mindhub.todolist.dtos.user.PatchUserRequestDTO;
import com.mindhub.todolist.dtos.user.PutUserRequestDTO;
import com.mindhub.todolist.exceptions.EmailAlreadyExistsException;
import com.mindhub.todolist.exceptions.InvalidUserException;
import com.mindhub.todolist.exceptions.UserNotFoundException;
import com.mindhub.todolist.models.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserEntity> getAllUsers();

    UserEntity getUserById(Long id) throws UserNotFoundException;

    UserEntity getUserByEmail(String email) throws UserNotFoundException;

    UserEntity createUser(NewUserRequestDTO newUserRequestDTO);

    void deleteUser(Long id) throws UserNotFoundException;

    UserEntity updatePutUser(Long id, PutUserRequestDTO putUserRequestDTO) throws UserNotFoundException, EmailAlreadyExistsException, InvalidUserException;

    UserEntity updatePatchUser(Long id, PatchUserRequestDTO patchUserRequestDTO) throws UserNotFoundException, InvalidUserException;

    Optional<UserEntity> findUserByEmail(String email);

}
