package com.example.demo.service;

import com.example.demo.dto.TaskDTO;
import com.example.demo.dtomapper.DTOMapper;
import com.example.demo.entity.Role;
import com.example.demo.entity.Task;
import com.example.demo.entity.TaskAssignment;
import com.example.demo.entity.User;
import com.example.demo.repository.TaskRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    TaskRepository taskRepository;
    @Mock
    private DTOMapper dtoMapper;

    @InjectMocks
    TaskService taskService;


    private User adminUser;
    private User assignedUser;
    private User unassignedUser;
    private Task task;
    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        //admin user
        adminUser = new User();
        adminUser.setId(1L);
        Role role = new Role();
        role.setId(1L);
        role.setName("Admin");
        adminUser.setRole(role);

        assignedUser = new User();
        assignedUser.setId(2L);
        Role user = new Role();
        user.setId(1L);
        user.setName("User");
        assignedUser.setRole(user);

        unassignedUser = new User();
        unassignedUser.setId(3L);
        Role user1 = new Role();
        user1.setId(1L);
        user1.setName("User");
        unassignedUser.setRole(user1);

        //mock the task
        Long taskId = 5L;
        task = new Task();
        task.setId(taskId);
        task.setTitle("Title 6");
        task.setPriority("High");
        task.setDescription("Test task");
        task.setStatus("Test");

        TaskAssignment taskAssignment = new TaskAssignment();
        taskAssignment.setId(1L);
        taskAssignment.setTask(task);
        taskAssignment.setUser(assignedUser);
        taskAssignment.setStatus("To Do");
        task.setAssignments(List.of(taskAssignment));

        //Mock TaskDTO
        TaskDTO taskDTO1 = new TaskDTO();
        taskDTO1.setId(5L);

        // Mock DTO Mapper
        lenient().when(dtoMapper.mapToTaskDTO(task, true, true)).thenReturn(taskDTO);
        lenient().when(dtoMapper.mapToTaskDTO(task, false, true)).thenReturn(taskDTO);

    }

    @Test
    void shouldReturnTaskWhenAdminRequests() {
        when(taskRepository.findById(5L)).thenReturn(Optional.of(task));

        assertDoesNotThrow(() -> {
            TaskDTO result = taskService.getTaskById(5L, adminUser);
            assertEquals(taskDTO, result);
        });
    }

    @Test
    void shouldReturnTaskWhenUserRequests() {
        when(taskRepository.findById(5L)).thenReturn(Optional.of(task));

        assertDoesNotThrow(()-> {
            TaskDTO result = taskService.getTaskById(5L, assignedUser);
            assertEquals(taskDTO, result);
        });
    }

    @Test
    void shouldThrowAccessDeniedExceptionWhenUnassignedUserRequests() {
        when(taskRepository.findById(5L)).thenReturn(Optional.of(task));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.getTaskById(5L, unassignedUser);
        });

        assertTrue(exception.getCause() instanceof AccessDeniedException);
    }

    @Test
    void createTaskTest() {

        Task task = new Task();
        task.setId(6L);
        task.setTitle("Title 6");
        task.setPriority("High");
        task.setDescription("Test task");
        task.setStatus("Test");

        when(taskRepository.save(task)).thenReturn(task);
        Task addedTask = taskService.createTask(task);
        Assertions.assertEquals(task.getId(),addedTask.getId());
    }

//    @Test
//    void getTaskByIdTest() {
//        Long taskId = 5L;
//        Task task = new Task();
//        task.setId(taskId);
//        task.setTitle("Title 6");
//        task.setPriority("High");
//        task.setDescription("Test task");
//        task.setStatus("Test");
//
//        User currentUser = new User();
//        currentUser.setId(1L);
//        currentUser.setUsername("testUser");
//
//        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
//        TaskDTO retrievedTask = taskService.getTaskById(taskId,currentUser);
//        assertEquals(taskId, retrievedTask.getId(), "Task ID should match");
//    }
}