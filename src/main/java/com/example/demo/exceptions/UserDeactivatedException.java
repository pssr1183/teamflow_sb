package com.example.demo.exceptions;

public class UserDeactivatedException extends RuntimeException{
    public UserDeactivatedException(String message) {
        super(message);
    }
}
