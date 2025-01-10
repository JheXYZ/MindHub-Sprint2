package com.mindhub.todolist.exceptions;

public class TaskNotFoundException extends Exception {

    public TaskNotFoundException(String message){
        super(message);
    }

    public TaskNotFoundException(){
        super("task was not found");
    }
}
