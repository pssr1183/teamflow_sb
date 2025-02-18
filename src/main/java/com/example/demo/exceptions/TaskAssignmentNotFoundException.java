package com.example.demo.exceptions;

public class TaskAssignmentNotFoundException extends RuntimeException{
    public TaskAssignmentNotFoundException(String message) {
        super(message);
    }
}
