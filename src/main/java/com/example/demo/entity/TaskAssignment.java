package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class TaskAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "task_id",referencedColumnName = "id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    private LocalDate assignedDate;

    private String status;

}
