package com.mindhub.todolist.exceptions;

public class InvalidTaskException extends Exception {
    public InvalidTaskException(String message) {
        super(message);
    }

    public InvalidTaskException() {
        super("invalid task");
    }
}
