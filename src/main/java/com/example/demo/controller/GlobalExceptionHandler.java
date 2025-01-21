package com.example.demo.controller;

import com.example.demo.entity.ErrorResponse;
import com.example.demo.exceptions.TaskNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<?> handleTaskNotFoundExceptions(TaskNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(),e.getMessage(),"Not found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
