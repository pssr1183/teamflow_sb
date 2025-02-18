package com.example.demo.service;

import com.example.demo.dto.TaskDTO;
import com.example.demo.dtomapper.DTOMapper;
import com.example.demo.entity.Role;
import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import com.example.demo.exceptions.AccessDeniedException;
import com.example.demo.exceptions.TaskNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

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
                ()-> new UserNotFoundException("User Not Found")
        );
        List<Task> tasks;
        Boolean canViewAllTasks = !user.getRoles().isEmpty()
                &&  user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(permission -> permission.getName().equalsIgnoreCase("TASK_VIEW_ALL"));

        if(!canViewAllTasks) {
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
        List<TaskDTO> taskDTOS = dtoMapper.mapToDTOList(tasks,canViewAllTasks,user);

        return taskDTOS;
    }


    public TaskDTO getTaskById(Long Id, User currentUser) {

        Boolean canViewAllTasks = !currentUser.getRoles().isEmpty() &&  currentUser.getRoles().stream().flatMap(role -> role.getPermissions().stream()).anyMatch(permission -> permission.getName().equalsIgnoreCase("TASK_VIEW_ALL"));
        Task task = taskRepository.findById(Id).orElseThrow(
                ()-> new TaskNotFoundException("Task not found with ID " + Id)
        );

        if(!canViewAllTasks) {

            if(task.getAssignments().isEmpty()) {
                throw new AccessDeniedException("Task is not assigned to anyone");
            }

            boolean isAssigned = task.getAssignments().stream()
                    .anyMatch(taskAssignment -> taskAssignment.getUser().getId().equals(currentUser.getId()));
            if(!isAssigned) {
                throw new AccessDeniedException("You are not authorized to access this task.");
            }
        }

        return dtoMapper.mapToTaskDTO(task,canViewAllTasks,true,currentUser);
    }


    @CacheEvict(value = "tasks", allEntries = true)
    public Task updateTask(Long Id, Task updatedTask) {

        return taskRepository.findById(Id).map(task -> {
            task.setDeadline(updatedTask.getDeadline());
            task.setDescription(updatedTask.getDescription());
            task.setStatus(updatedTask.getStatus());
            task.setPriority(updatedTask.getPriority());
            task.setTitle(updatedTask.getTitle());
            return taskRepository.save(task);
        }).orElseThrow(
                ()-> new TaskNotFoundException("Task not found with ID " + Id)
        );
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public Task createTask(Task task) {

        return taskRepository.save(task);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public Task deleteTask(Long id) {

       Task task =  taskRepository.findById(id).orElseThrow(()-> new TaskNotFoundException("Task not found with ID " + id));
       taskRepository.deleteById(id);

       return task;
    }

}
