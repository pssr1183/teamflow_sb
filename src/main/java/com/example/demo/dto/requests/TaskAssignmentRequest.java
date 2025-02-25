package com.example.demo.dto.requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskAssignmentRequest {

    @NotEmpty(message = "Task Assignment status cannot be empty")
    private String status;
}
