package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class TaskAssignmentRequest {
    private Long userId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDate assignmentDate;
    private String status;
}
