package com.example.demo.controller;

import com.example.demo.exceptions.ApiResponse;
import com.example.demo.exceptions.TaskNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleTaskNotFoundExceptions(TaskNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiResponse> handleAccessDeniedExceptions(AccessDeniedException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN,e.getMessage(),request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponse> handleUserNotFoundExceptions(UserNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND,e.getMessage(),request);
    }


    private ResponseEntity<ApiResponse> buildErrorResponse(HttpStatus status, String message,HttpServletRequest request) {
        ApiResponse errorResponse = new ApiResponse(
               LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, status);
    }
}
