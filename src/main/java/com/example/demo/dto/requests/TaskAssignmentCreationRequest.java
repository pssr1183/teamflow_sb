package com.example.demo.dto.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class TaskAssignmentCreationRequest extends TaskAssignmentRequest {

    @NotNull(message = "UserId cannot be null")
    private Long userId;

    @JsonInclude(JsonInclude.Include.NON_NULL)

    @NotNull(message = "assignedDate cannot be empty")
    @FutureOrPresent(message = "Deadline must be today or a future date")
    private LocalDate assignmentDate;
}
