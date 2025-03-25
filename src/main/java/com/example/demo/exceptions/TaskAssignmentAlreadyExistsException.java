package com.example.demo.exceptions;

public class TaskAssignmentAlreadyExistsException extends RuntimeException{
    public TaskAssignmentAlreadyExistsException(String message) {
        super(message);
    }
}
