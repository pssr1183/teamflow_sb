package com.example.demo.service;

import com.example.demo.entity.Task;
import com.example.demo.repository.TaskRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    TaskRepository taskRepository;
    @InjectMocks
    TaskService taskService;


    @Test
    void createTaskTest() {

        Task task = new Task();
        task.setId(6L);
        task.setTitle("Title 6");
        task.setAssignedUser("Dummy User");
        task.setPriority("High");
        task.setDescription("Test task");
        task.setStatus("Test");

        Mockito.when(taskRepository.save(task)).thenReturn(task);
        Task addedTask = taskService.createTask(task);
        Assertions.assertEquals(task.getId(),addedTask.getId());
    }

    @Test
    void getTaskByIdTest() {
        Long taskId = 5L;
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Title 6");
        task.setAssignedUser("Dummy User");
        task.setPriority("High");
        task.setDescription("Test task");
        task.setStatus("Test");

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Optional<Task> retrievedTask = taskService.getTaskById(taskId);
        assertEquals(taskId, retrievedTask.get().getId(), "Task ID should match");
    }

}