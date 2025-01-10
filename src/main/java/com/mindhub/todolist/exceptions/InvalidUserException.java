package com.mindhub.todolist.exceptions;

import java.util.List;

public class InvalidUserException extends Exception{

    public InvalidUserException(String message){
        super(message);
    }

    public InvalidUserException() {
        super("invalid user");
    }
}
