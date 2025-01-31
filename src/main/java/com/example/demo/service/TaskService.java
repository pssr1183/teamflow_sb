package com.example.demo.service;

import com.example.demo.dto.TaskDTO;
import com.example.demo.dtomapper.DTOMapper;
import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import com.example.demo.exceptions.TaskNotFoundException;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DTOMapper dtoMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Cacheable(value = "tasks")
    public List<TaskDTO> getAllTasks(String username, String status, String priority) {

        User user = userRepository.findByUsername(username).orElseThrow(
                ()-> new RuntimeException("User Not Found")
        );
        List<Task> tasks;
        Boolean isAdmin = user.getRole() != null &&  user.getRole().getName().equalsIgnoreCase("Admin");
        if(!isAdmin) {
            tasks = taskRepository.findTasksByUserId(user.getId());
        } else {
            tasks = taskRepository.findAll();
        }
        if(status != null) {
           tasks = tasks.stream()
                    .filter(task -> task.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }
        if(priority != null) {
            tasks = tasks
                    .stream()
                    .filter(task -> task.getPriority().equalsIgnoreCase(priority))
                    .collect(Collectors.toList());
        }
        List<TaskDTO> taskDTOS = dtoMapper.mapToDTOList(tasks);

        return taskDTOS;
    }


    public TaskDTO getTaskById(Long Id, User currentUser) {

        Boolean isAdmin = currentUser.getRole() != null &&  currentUser.getRole().getName().equalsIgnoreCase("Admin");
        Task task = taskRepository.findById(Id).orElseThrow(
                ()-> new TaskNotFoundException("Task not found with ID " + Id)
        );
        if(!isAdmin) {
            if(task.getAssignments() != null) {
                boolean isAssigned = task.getAssignments().stream()
                        .anyMatch(taskAssignment -> taskAssignment.getUser().getId().equals(currentUser.getId()));
                if(!isAssigned) {
                    try {
                        throw new AccessDeniedException("You are not authorized to access this task.");
                    } catch (AccessDeniedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return dtoMapper.mapToTaskDTO(task,isAdmin,true);
    }


    @CacheEvict(value = "tasks", allEntries = true)
    public Optional<Task> updateTask(Long Id, Task updatedTask) {
        return taskRepository.findById(Id).map(task -> {
            task.setDeadline(updatedTask.getDeadline());
            task.setDescription(updatedTask.getDescription());
            task.setStatus(updatedTask.getStatus());
            task.setPriority(updatedTask.getPriority());
            task.setTitle(updatedTask.getTitle());
            return taskRepository.save(task);
        });
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public Optional<?> deleteTask(Long id) {
       Optional<Task> task =  taskRepository.findById(id);
       taskRepository.deleteById(id);
       return task;
    }

}
