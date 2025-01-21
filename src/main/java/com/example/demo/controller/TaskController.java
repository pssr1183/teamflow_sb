package com.example.demo.controller;

import com.example.demo.entity.ErrorResponse;
import com.example.demo.entity.Task;
import com.example.demo.exceptions.TaskNotFoundException;
import com.example.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id).orElseThrow(
                () -> new TaskNotFoundException("Task not found with ID " + id));
        return ResponseEntity.ok(task);

    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Validated @RequestBody Task task) {
        Task updatedTask = taskService.updateTask(id, task).orElseThrow(
                () -> new TaskNotFoundException("Task not found with ID " + id)
        );
        return ResponseEntity.ok(updatedTask);
    }

    @PostMapping
    public Task createTask(@Validated @RequestBody Task task) {
        return taskService.createTask(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id).orElseThrow( () -> new TaskNotFoundException("Task not found with ID so cannot delete " + id));
        return ResponseEntity.noContent().build();
    }
}
