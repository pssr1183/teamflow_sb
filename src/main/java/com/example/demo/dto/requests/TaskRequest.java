package com.example.demo.dto.requests;

import com.example.demo.dto.TaskAssignmentDTO;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TaskRequest implements Serializable {

    private Long id;

    @NotEmpty(message = "Title cannot be empty")
    private String title;

    private String description;

    @NotNull(message = "Deadline cannot be empty")
    @FutureOrPresent(message = "Deadline must be today or a future date")
    private LocalDate deadline;

    @NotEmpty(message = "Priority cannot be empty")
    private String priority;

    @NotEmpty(message = "Status cannot be empty")
    private String status;

    private List<TaskAssignmentDTO> assignments;
}
