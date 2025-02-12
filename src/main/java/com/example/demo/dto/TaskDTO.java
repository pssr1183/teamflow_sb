package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TaskDTO implements Serializable {

    private Long id;
    private String title;
    private String description;
    private LocalDate deadline;
    private String priority;
    private String status;
    private List<TaskAssignmentDTO> assignments;
}
