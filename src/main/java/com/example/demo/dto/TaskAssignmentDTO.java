package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class TaskAssignmentDTO implements Serializable {
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDTO user;
    private LocalDate assignmentDate;
    private String status;
}
