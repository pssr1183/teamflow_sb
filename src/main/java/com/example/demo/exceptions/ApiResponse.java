package com.example.demo.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ApiResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> fieldErrors;
}
