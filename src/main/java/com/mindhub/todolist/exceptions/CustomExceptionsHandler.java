package com.mindhub.todolist.exceptions;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class CustomExceptionsHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomExceptionsHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFoundExceptionHandler(UserNotFoundException userNotFoundException) {
        return finalResponse(userNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> usernameNotFoundExceptionHandler(UsernameNotFoundException usernameNotFoundException){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> taskNotFoundExceptionHandler(TaskNotFoundException taskNotFoundException) {
        return finalResponse(taskNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ErrorResponse> invalidUserExceptionHandler(InvalidUserException invalidUserException) {
        List<String> errors = parseToListFromString(invalidUserException.getMessage());
        if (!errors.isEmpty())
            return finalResponse(errors, HttpStatus.BAD_REQUEST);
        return finalResponse(invalidUserException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTaskException.class)
    public ResponseEntity<ErrorResponse> invalidTaskExceptionHandler(InvalidTaskException invalidTaskException) {
        return finalResponse(invalidTaskException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> UnauthorizedExceptionHandler(UnauthorizedException unauthorizedException) {
        return finalResponse(unauthorizedException.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> emailAlreadyExistsHandler(EmailAlreadyExistsException emailAlreadyExistsException) {
        return finalResponse(emailAlreadyExistsException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidArgument(MethodArgumentNotValidException methodArgumentNotValidException) {
        return finalResponse(
                methodArgumentNotValidException
                        .getBindingResult()
                        .getFieldErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList()
                , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException ex) {
        if (ex.getCause() == null || !ex.getCause().getMessage().contains("not one of the values accepted for Enum"))
            return finalResponse("invalid JSON request", HttpStatus.BAD_REQUEST);

        /*log.error(ex.getMessage());*/
        String validValues = ex.getMessage().substring(ex.getMessage().indexOf("["));
        if (ex.getMessage().contains("UserAuthority"))
            return finalResponse(
                    "invalid authority. Accepted values: " + validValues,
                    HttpStatus.BAD_REQUEST);
        if (ex.getMessage().contains("TaskStatus"))
            return finalResponse(
                    "invalid taskStatus. Accepted values: " + validValues,
                    HttpStatus.BAD_REQUEST);

        return finalResponse("invalid value provided. Accepted values: " + validValues, HttpStatus.BAD_REQUEST);
    }

    public record ErrorResponse(List<String> errors) {}

    private static List<String> parseToListFromString(String errorMessage) {
        List<String> errorsList = new ArrayList<>();
        if (errorMessage.startsWith("[") && errorMessage.endsWith("]")) {
            // this regex splits the string when '[' , ']' and/or ',' are found.
            // E.g. String '[invalid email, invalid password]' -> List<String> ["", "invalid email", "invalid password"]
            errorsList.addAll(Arrays.stream(errorMessage.split("\\[|]|,\\s*")).toList());

            // this eliminates first index because it's always "", resulting in only the messages.
            // E.g. List<String> ["invalid email", "invalid password"]
            errorsList.remove(0);
        }
        return errorsList;
    }

    private ResponseEntity<ErrorResponse> finalResponse(String errorMessage, HttpStatus codeStatus) {
        return new ResponseEntity<>(response(errorMessage), codeStatus);
    }

    private ResponseEntity<ErrorResponse> finalResponse(List<String> errorMessageList, HttpStatus codeStatus) {
        return new ResponseEntity<>(response(errorMessageList), codeStatus);
    }

    private ErrorResponse response(String errorMessage) {
        return new ErrorResponse(List.of(errorMessage));
    }

    private ErrorResponse response(List<String> errorMessageList) {
        return new ErrorResponse(errorMessageList);
    }

}
