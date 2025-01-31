package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tasks")
@Getter
@Setter

public class Task implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private LocalDate deadline;

    private String priority;

    private String status; // Example values: To Do, In Progress, Done

    @OneToMany(mappedBy = "task")// One-to-many relationship with TaskAssignment
    private List<TaskAssignment> assignments;
}
