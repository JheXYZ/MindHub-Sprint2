package com.mindhub.todolist.services.implementations;

import com.mindhub.todolist.dtos.user.*;
import com.mindhub.todolist.exceptions.EmailAlreadyExistsException;
import com.mindhub.todolist.exceptions.InvalidUserException;
import com.mindhub.todolist.exceptions.UserNotFoundException;
import com.mindhub.todolist.models.UserAuthority;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.UserRepository;
import com.mindhub.todolist.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImp.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<List<ExtendedUserDTO>> getAllUsersRequest() {
        return ResponseEntity.ok(getAllUsers().stream().map(ExtendedUserDTO::new).toList());
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public ResponseEntity<ExtendedUserDTO> getUserByIdRequest(Long id) throws UserNotFoundException {
        return ResponseEntity.ok(new ExtendedUserDTO(getUserById(id)));
    }

    @Override
    public UserEntity getUserById(Long id) throws UserNotFoundException {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("user was not found"));
    }

    @Override
    public ResponseEntity<UserDTO> getUserByEmailRequest(String email) throws UserNotFoundException {
        return ResponseEntity.ok(new UserDTO(getUserByEmail(email)));
    }

    @Override
    public UserEntity getUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user with email '" + email + " was not found"));
    }

    @Override
    public UserEntity createUser(Authentication authentication, NewUserRequestDTO newUserRequestDTO) {
        UserEntity user = new UserEntity(newUserRequestDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (authentication != null && authentication
                .getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"))
        )
            user.setAuthority(newUserRequestDTO.authority());

        return userRepository.save(user);
    }

    @Override
    public ResponseEntity<UserDTO> createUserRequest(Authentication auth, NewUserRequestDTO newUserRequestDTO) {
        return new ResponseEntity<>(new UserDTO(createUser(auth,newUserRequestDTO)), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> deleteUserRequest(String email) throws UserNotFoundException {
        deleteUser(getUserByEmail(email));
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> deleteUserByIdRequest(Authentication authentication, Long id) throws UserNotFoundException {
        deleteUser(getUserByEmail(authentication.getName()));
        log.info("Deleted user with id: {} by: {}", id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @Override
    public void deleteUser(UserEntity user) throws UserNotFoundException {
        if (!userRepository.existsById(user.getId()))
            throw new UserNotFoundException("user was not found");
        userRepository.delete(user);
    }


    @Override
    public ResponseEntity<UserDTO> updatePutUserByIdRequest(Long id, PutUserRequestDTO putUserRequestDTO) throws UserNotFoundException, EmailAlreadyExistsException, InvalidUserException {
        return ResponseEntity.ok(new UserDTO(updatePutUser(id, putUserRequestDTO, true)));
    }

    @Override
    public ResponseEntity<UserDTO> updatePutUserRequest(String email, PutUserRequestDTO putUserRequestDTO) throws UserNotFoundException, EmailAlreadyExistsException, InvalidUserException {
        Long id = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user with email '" + email + "'was not found"))
                .getId();
        return ResponseEntity.ok(new UserDTO(updatePutUser(id, putUserRequestDTO, false)));
    }

    @Override
    public UserEntity updatePutUser(Long id, PutUserRequestDTO putUserRequestDTO, boolean isRequestFromAdmin) throws UserNotFoundException, EmailAlreadyExistsException, InvalidUserException {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user with id '" + id + "' was not found"));

        makeUpdatesPutUser(user, putUserRequestDTO, isRequestFromAdmin);
        return userRepository.save(user);
    }

    @Override
    public ResponseEntity<ExtendedUserDTO> updatePatchUserByIdRequest(Long id, PatchUserRequestDTO patchUserRequestDTO) throws UserNotFoundException, InvalidUserException {
        return ResponseEntity.ok(new ExtendedUserDTO(updatePatchUser(id, patchUserRequestDTO, true)));
    }

    @Override
    public ResponseEntity<UserDTO> updatePatchUserRequest(String email, PatchUserRequestDTO patchUserRequestDTO) throws UserNotFoundException, InvalidUserException {
        Long id = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user with email '" + email + "'was not found"))
                .getId();
        return ResponseEntity.ok(new UserDTO(updatePatchUser(id, patchUserRequestDTO, false)));
    }

    @Override
    public UserEntity updatePatchUser(Long id, PatchUserRequestDTO patchUserRequestDTO, boolean isRequestFromAdmin) throws UserNotFoundException, InvalidUserException {
        UserEntity user = userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("user with id '" + id + "' was not found"));
        makeUpdatesPatchUser(user, patchUserRequestDTO, isRequestFromAdmin);
        return userRepository.save(user);
    }


    private void makeUpdatesPutUser(UserEntity user, PutUserRequestDTO userUpdates, boolean isRequestFromAdmin) throws EmailAlreadyExistsException, InvalidUserException {
        user.setUsername(userUpdates.username());
        if (!userUpdates.email().equals(user.getEmail()) && userRepository.existsByEmail(userUpdates.email()))
            throw new EmailAlreadyExistsException("email '" + userUpdates.email() + "' is already taken");
        user.setEmail(userUpdates.email());

        if (passwordEncoder.matches(userUpdates.password(), user.getPassword()))
            throw new InvalidUserException("password can not be the same as the old one");
        user.setPassword(passwordEncoder.encode(userUpdates.password()));

        if (isRequestFromAdmin && userUpdates.authority() != null)
            user.setAuthority(userUpdates.authority());
    }

    private void makeUpdatesPatchUser(UserEntity user, PatchUserRequestDTO userUpdates, boolean isRequestFromAdmin) throws InvalidUserException {
        List<String> errors = new ArrayList<>();

        updateUsernameIfValid(user, userUpdates, errors);
        updateEmailIfValid(user, userUpdates, errors);
        updatePasswordIfValid(user, userUpdates, errors);
        updateAuthorityIfValid(user, userUpdates, isRequestFromAdmin, errors);

        if (!errors.isEmpty())
            throw new InvalidUserException(errors.toString());
    }

    private void updatePasswordIfValid(UserEntity user, PatchUserRequestDTO userUpdates, List<String> errors) {
        List<String> currentErrors = new ArrayList<>();
        if (userUpdates.password() != null) {
            if (!passwordEncoder.matches(userUpdates.password(), user.getPassword())) {
                if (userUpdates.password().length() < 6 || userUpdates.password().length() > 40)
                    currentErrors.add("password must be between 6 and 40 characters");
                if (userUpdates.password().isBlank())
                    currentErrors.add("password must not be empty");
                if (currentErrors.isEmpty())
                    user.setPassword(passwordEncoder.encode(userUpdates.password()));
            } else
                currentErrors.add("password can not be the same as the old one");
        }
        errors.addAll(currentErrors);
    }

    private void updateEmailIfValid(UserEntity user, PatchUserRequestDTO userUpdates, List<String> errors) {
        List<String> currentErrors = new ArrayList<>();
        if (userUpdates.email() != null && !userUpdates.email().equals(user.getEmail())) {
            if (userUpdates.email().isBlank())
                currentErrors.add("email must not be empty");
            /*
                The following regex validates email addresses following these rules:
                -The username can contain letters, numbers, hyphens, underscores, and dots. E.g., valid-ex.amp_le@__.__
                -It must include an @ symbol.
                -The domain must contain at least one valid subdomain followed by a dot. E.g., __@valid.__
                -The domain must end with an extension of 2 to 4 characters. E.g., __@__.com | __@__.ar
            */
            if (!user.getEmail().equals(userUpdates.email()) && userRepository.existsByEmail(userUpdates.email()))
                currentErrors.add("email '" + user.getEmail() + "' is already taken");
            if (currentErrors.isEmpty())
                user.setEmail(userUpdates.email());
        }
        errors.addAll(currentErrors);
    }

    private void updateUsernameIfValid(UserEntity user, PatchUserRequestDTO userUpdates, List<String> errors) {
        String username = userUpdates.username();
        List<String> currentErrors = new ArrayList<>();
        if (username != null && !username.equals(user.getUsername())) {
            if (userUpdates.username().contains(" "))
                currentErrors.add("username can not contain whitespaces");
            if (username.isBlank())
                currentErrors.add("username must not be empty");
            if (currentErrors.isEmpty())
                user.setUsername(username);
        }
        errors.addAll(currentErrors);
    }

    private void updateAuthorityIfValid(UserEntity user, PatchUserRequestDTO userUpdates, boolean isRequestFromAdmin, List<String> errors) {
        if (isRequestFromAdmin && userUpdates.authority() != null)
            user.setAuthority(userUpdates.authority());
    }

    public Optional<UserEntity> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
