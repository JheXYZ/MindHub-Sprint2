package com.mindhub.todolist.exceptions;

public class InvalidUserException extends Exception {

    public InvalidUserException(String message) {
        super(message);
    }

    public InvalidUserException() {
        super("invalid user");
    }
}
