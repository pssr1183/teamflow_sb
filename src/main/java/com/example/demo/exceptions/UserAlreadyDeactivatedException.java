package com.example.demo.exceptions;

public class UserAlreadyDeactivatedException extends RuntimeException{
    public UserAlreadyDeactivatedException(String message) {
        super(message);
    }
}
