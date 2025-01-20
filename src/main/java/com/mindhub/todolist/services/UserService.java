package com.mindhub.todolist.services;

import com.mindhub.todolist.dtos.user.*;
import com.mindhub.todolist.exceptions.EmailAlreadyExistsException;
import com.mindhub.todolist.exceptions.InvalidUserException;
import com.mindhub.todolist.exceptions.UnauthorizedException;
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

    UserEntity createUser(String email, NewUserRequestDTO newUserRequestDTO) throws UnauthorizedException, UserNotFoundException;

    void deleteUser(UserEntity user) throws UserNotFoundException;

    UserEntity updatePutUser(Long id, PutUserRequestDTO putUserRequestDTO, boolean isRequestFromAdmin) throws UserNotFoundException, EmailAlreadyExistsException, InvalidUserException;

    UserEntity updatePatchUser(Long id, PatchUserRequestDTO patchUserRequestDTO, boolean isRequestFromAdmin) throws UserNotFoundException, InvalidUserException;

    ResponseEntity<List<ExtendedUserDTO>> getAllUsersRequest();

    ResponseEntity<ExtendedUserDTO> getUserByIdRequest(Long id) throws UserNotFoundException;

    ResponseEntity<UserDTO> getUserByEmailRequest(String email) throws UserNotFoundException;

    ResponseEntity<UserDTO> createUserRequest(Authentication auth, NewUserRequestDTO newUserRequestDTO) throws UnauthorizedException, UserNotFoundException;

    ResponseEntity<?> deleteUserRequest(String email) throws UserNotFoundException;

    ResponseEntity<?> deleteUserByIdRequest(Authentication authentication, Long id) throws UserNotFoundException;

    ResponseEntity<ExtendedUserDTO> updatePutUserByIdRequest(Long id, PutUserRequestDTO putUserRequestDTO) throws UserNotFoundException, EmailAlreadyExistsException, InvalidUserException;

    ResponseEntity<UserDTO> updatePutUserRequest(String email, PutUserRequestDTO putUserRequestDTO) throws UserNotFoundException, EmailAlreadyExistsException, InvalidUserException;

    ResponseEntity<ExtendedUserDTO> updatePatchUserByIdRequest(Long id, PatchUserRequestDTO patchUserRequestDTO) throws UserNotFoundException, InvalidUserException;

    ResponseEntity<UserDTO> updatePatchUserRequest(String email, PatchUserRequestDTO patchUserRequestDTO) throws UserNotFoundException, InvalidUserException;

    Optional<UserEntity> findUserByEmail(String email);

}
