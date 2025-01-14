package com.mindhub.todolist.services.implementations;

import com.mindhub.todolist.dtos.user.NewUserRequestDTO;
import com.mindhub.todolist.dtos.user.PatchUserRequestDTO;
import com.mindhub.todolist.dtos.user.PutUserRequestDTO;
import com.mindhub.todolist.dtos.user.UserDTO;
import com.mindhub.todolist.exceptions.EmailAlreadyExistsException;
import com.mindhub.todolist.exceptions.InvalidUserException;
import com.mindhub.todolist.exceptions.UnauthorizedException;
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

    public ResponseEntity<List<UserDTO>> getAllUsersRequest() {
        return ResponseEntity.ok(getAllUsers().stream().map(UserDTO::new).toList());
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public ResponseEntity<UserDTO> getUserByIdRequest(Long id) throws UserNotFoundException {
        return ResponseEntity.ok(new UserDTO(getUserById(id)));
    }

    @Override
    public UserEntity getUserById(Long id) throws UserNotFoundException {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("user was not found"));
    }

    public ResponseEntity<UserDTO> getUserByEmailRequest(String email) throws UserNotFoundException {
        return ResponseEntity.ok(new UserDTO(getUserByEmail(email)));
    }

    @Override
    public UserEntity getUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user with email '" + email + " was not found"));
    }

    @Override
    public UserEntity createUser(NewUserRequestDTO newUserRequestDTO) {
        UserEntity user = new UserEntity(newUserRequestDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public ResponseEntity<UserDTO> createUserRequest(NewUserRequestDTO newUserRequestDTO){
        return new ResponseEntity<>(new UserDTO(createUser(newUserRequestDTO)), HttpStatus.CREATED);
    }

    public ResponseEntity<?> deleteUserRequest(String email) throws UserNotFoundException, UnauthorizedException {
        deleteUser(getUserByEmail(email).getId());
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> deleteUserRequest(Authentication authentication, Long id) throws UnauthorizedException, UserNotFoundException {
        /*if (authentication
                .getAuthorities()
                .stream()
                .noneMatch(authority -> authority.getAuthority().equals("ADMIN"))
        )
            throw new UnauthorizedException();*/

        deleteUser(id);
        log.info("Deleted user with id: {} by: {}", id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @Override
    public void deleteUser(Long id) throws UserNotFoundException {
        if (!userRepository.existsById(id))
            throw new UserNotFoundException("user with id '" + id + "' was not found");
        userRepository.deleteById(id);
    }

    public ResponseEntity<UserDTO> updatePutUserRequest(Long id, PutUserRequestDTO putUserRequestDTO) throws UserNotFoundException, EmailAlreadyExistsException, InvalidUserException {
        return ResponseEntity.ok(new UserDTO(updatePutUser(id, putUserRequestDTO)));
    }

    public ResponseEntity<UserDTO> updatePutUserRequest(String email, PutUserRequestDTO putUserRequestDTO) throws UserNotFoundException, EmailAlreadyExistsException, InvalidUserException {
        Long id = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user with email '" + email + "'was not found"))
                .getId();
        return ResponseEntity.ok(new UserDTO(updatePutUser(id, putUserRequestDTO)));
    }

    @Override
    public UserEntity updatePutUser(Long id, PutUserRequestDTO putUserRequestDTO) throws UserNotFoundException, EmailAlreadyExistsException, InvalidUserException {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user with id '" + id + "' was not found"));

        makeUpdatesPutUser(user, putUserRequestDTO);
        return userRepository.save(user);
    }

    public ResponseEntity<UserDTO> updatePatchUserRequest(Long id, PatchUserRequestDTO patchUserRequestDTO) throws UserNotFoundException, InvalidUserException {
        return ResponseEntity.ok(new UserDTO(updatePatchUser(id, patchUserRequestDTO)));
    }

    public ResponseEntity<UserDTO> updatePatchUserRequest(String email, PatchUserRequestDTO patchUserRequestDTO) throws UserNotFoundException, InvalidUserException {
        Long id = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user with email '" + email + "'was not found"))
                .getId();
        return ResponseEntity.ok(new UserDTO(updatePatchUser(id, patchUserRequestDTO)));
    }

    @Override
    public UserEntity updatePatchUser(Long id, PatchUserRequestDTO patchUserRequestDTO) throws UserNotFoundException, InvalidUserException {
        UserEntity user = userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("user with id '" + id + "' was not found"));
        makeUpdatesPatchUser(user, patchUserRequestDTO);
        return userRepository.save(user);
    }


    private void makeUpdatesPutUser(UserEntity user, PutUserRequestDTO userUpdates) throws EmailAlreadyExistsException, InvalidUserException {
        user.setUsername(userUpdates.username());
        if (!userUpdates.email().equals(user.getEmail()) && userRepository.existsByEmail(userUpdates.email()))
            throw new EmailAlreadyExistsException("email '" + userUpdates.email() + "' is already taken");
        user.setEmail(userUpdates.email());
        if (passwordEncoder.matches(userUpdates.password(), user.getPassword()))
            throw new InvalidUserException("password can not be the same as the old one");
        user.setPassword(passwordEncoder.encode(userUpdates.password()));
    }

    private void makeUpdatesPatchUser(UserEntity user, PatchUserRequestDTO userUpdates) throws InvalidUserException {
        List<String> errors = new ArrayList<>();

        updateUsernameIfValid(user, userUpdates, errors);
        updateEmailIfValid(user, userUpdates, errors);
        updatePasswordIfValid(user, userUpdates, errors);
        updateAuthorityIfValid(user, userUpdates, errors);

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
            if (!userUpdates.email().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
                currentErrors.add("invalid email");
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

    private void updateAuthorityIfValid(UserEntity user, PatchUserRequestDTO userUpdates, List<String> errors) {
        if (userUpdates.authority() != null && !user.getAuthority().equals(userUpdates.authority())){
            user.setAuthority(userUpdates.authority());
        }
    }

    public Optional<UserEntity> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
