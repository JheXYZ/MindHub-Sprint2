package com.mindhub.todolist.services;

import com.mindhub.todolist.dtos.user.NewUserRequestDTO;
import com.mindhub.todolist.dtos.user.PatchUserRequestDTO;
import com.mindhub.todolist.dtos.user.PutUserRequestDTO;
import com.mindhub.todolist.dtos.user.UserDTO;
import com.mindhub.todolist.exceptions.EmailAlreadyExistsException;
import com.mindhub.todolist.exceptions.InvalidUserException;
import com.mindhub.todolist.exceptions.UserNotFoundException;
import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.UserEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {

        ResponseEntity<List<UserDTO>> getAllUsersDTO();

        List<UserEntity> getAllUsers();

        ResponseEntity<UserDTO> getUserDTOById(Long id) throws UserNotFoundException;

        UserEntity getUserById(Long id) throws UserNotFoundException;

        ResponseEntity<UserDTO> createUser (NewUserRequestDTO newUserRequestDTO);

        ResponseEntity<?> deleteUser(Long id) throws UserNotFoundException;

        ResponseEntity<UserDTO> updatePutUser(Long id, PutUserRequestDTO putUserRequestDTO) throws UserNotFoundException, EmailAlreadyExistsException;

        ResponseEntity<UserDTO> updatePatchUser(Long id, PatchUserRequestDTO patchUserRequestDTO) throws UserNotFoundException, InvalidUserException;

        Optional<UserEntity> findUserByEmail(String email);

}
