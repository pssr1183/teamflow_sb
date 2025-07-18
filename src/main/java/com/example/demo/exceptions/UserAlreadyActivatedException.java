package com.example.demo.exceptions;

public class UserAlreadyActivatedException extends RuntimeException{
    public UserAlreadyActivatedException(String message) {
        super(message);
    }
}
