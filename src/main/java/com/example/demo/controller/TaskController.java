package com.example.demo.controller;

import com.example.demo.dto.TaskDTO;
import com.example.demo.dtomapper.DTOMapper;
import com.example.demo.entity.ErrorResponse;
import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import com.example.demo.exceptions.TaskNotFoundException;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private DTOMapper dtoMapper;

    @GetMapping
    public List<?> getAllTasks(
            @RequestParam(value = "status",required = false) String status,
            @RequestParam(value = "priority",required = false) String priority,
            Principal principal
    ) {
        List<TaskDTO> taskDTOs = taskService.getAllTasks(principal.getName(),status,priority);
        return taskDTOs;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        TaskDTO taskDTO = taskService.getTaskById(id,user);
        return ResponseEntity.ok(taskDTO);
    }

    @PreAuthorize("hasRole('Admin')")
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Validated @RequestBody Task task) {
        Task updatedTask = taskService.updateTask(id, task);
        return ResponseEntity.ok(updatedTask);
    }

    @PreAuthorize("hasRole('Admin')")
    @PostMapping
    public Task createTask(@Validated @RequestBody Task task) {
        return taskService.createTask(task);
    }

    @PreAuthorize("hasRole('Admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
