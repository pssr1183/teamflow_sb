package com.example.demo.controller;

import com.example.demo.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleTaskNotFoundExceptions(TaskNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler(TaskAssignmentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleTaskAssignmentNotFoundExceptions(TaskAssignmentNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler(PermissionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handlePermissionNotFoundExceptions(PermissionNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiResponse> handleAccessDeniedExceptions(AccessDeniedException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN,e.getMessage(),request);
    }

    @ExceptionHandler(com.example.demo.exceptions.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiResponse> handleAccessDeniedExceptions(com.example.demo.exceptions.AccessDeniedException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN,e.getMessage(),request);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponse> NoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND,e.getMessage(),request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponse> handleUserNotFoundExceptions(UserNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND,e.getMessage(),request);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponse> handleRoleNotFoundExceptions(RoleNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND,e.getMessage(),request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleInvalidJson(HttpMessageNotReadableException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST,e.getMessage(),request);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse> handleTokenExpiredException(TokenExpiredException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.REQUEST_TIMEOUT,e.getMessage(),request);
    }

    @ExceptionHandler(TaskAssignmentAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> TaskAssignmentAlreadyExistsException(TaskAssignmentAlreadyExistsException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST,e.getMessage(),request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(),fieldError.getDefaultMessage()));
        return buildErrorResponse(HttpStatus.BAD_REQUEST,"Some of the fields are missing",request,errors);
    }


    private ResponseEntity<ApiResponse> buildErrorResponse(HttpStatus status, String message,HttpServletRequest request) {
        return buildErrorResponse(status,message,request,null);
    }

    private ResponseEntity<ApiResponse> buildErrorResponse(HttpStatus status, String message,HttpServletRequest request,Map<String,String> fieldErrors) {
        ApiResponse errorResponse = new ApiResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                fieldErrors
        );

        return new ResponseEntity<>(errorResponse, status);
    }
}
