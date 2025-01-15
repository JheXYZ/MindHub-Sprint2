package com.mindhub.todolist.services;

import com.mindhub.todolist.dtos.user.NewUserRequestDTO;
import com.mindhub.todolist.dtos.user.PatchUserRequestDTO;
import com.mindhub.todolist.dtos.user.PutUserRequestDTO;
import com.mindhub.todolist.dtos.user.UserDTO;
import com.mindhub.todolist.exceptions.EmailAlreadyExistsException;
import com.mindhub.todolist.exceptions.InvalidUserException;
import com.mindhub.todolist.exceptions.UserNotFoundException;
import com.mindhub.todolist.models.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserEntity> getAllUsers();

    UserEntity getUserById(Long id) throws UserNotFoundException;

    UserEntity getUserByEmail(String email) throws UserNotFoundException;

    UserEntity createUser(Authentication authentication, NewUserRequestDTO newUserRequestDTO);

    void deleteUser(Long id) throws UserNotFoundException;

    UserEntity updatePutUser(Long id, PutUserRequestDTO putUserRequestDTO) throws UserNotFoundException, EmailAlreadyExistsException, InvalidUserException;

    UserEntity updatePatchUser(Long id, PatchUserRequestDTO patchUserRequestDTO) throws UserNotFoundException, InvalidUserException;

    ResponseEntity<List<UserDTO>> getAllUsersRequest();

    ResponseEntity<UserDTO> getUserByIdRequest(Long id) throws UserNotFoundException;

    ResponseEntity<UserDTO> getUserByEmailRequest(String email) throws UserNotFoundException;

    ResponseEntity<UserDTO> createUserRequest(Authentication auth, NewUserRequestDTO newUserRequestDTO);

    ResponseEntity<?> deleteUserRequest(String email) throws UserNotFoundException;

    ResponseEntity<?> deleteUserRequest(Authentication authentication, Long id) throws UserNotFoundException;

    ResponseEntity<UserDTO> updatePutUserByIdRequest(Long id, PutUserRequestDTO putUserRequestDTO) throws UserNotFoundException, EmailAlreadyExistsException, InvalidUserException;

    ResponseEntity<UserDTO> updatePutUserRequest(String email, PutUserRequestDTO putUserRequestDTO) throws UserNotFoundException, EmailAlreadyExistsException, InvalidUserException;

    ResponseEntity<UserDTO> updatePatchUserByIdRequest(Long id, PatchUserRequestDTO patchUserRequestDTO) throws UserNotFoundException, InvalidUserException;

    ResponseEntity<UserDTO> updatePatchUserRequest(String email, PatchUserRequestDTO patchUserRequestDTO) throws UserNotFoundException, InvalidUserException;

    Optional<UserEntity> findUserByEmail(String email);

}
