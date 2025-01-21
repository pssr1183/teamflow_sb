package com.example.demo.service;

import com.example.demo.entity.Task;
import com.example.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Cacheable(value = "tasks")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }


    public Optional<Task> getTaskById(Long Id) {
        return taskRepository.findById(Id);
    }


    @CacheEvict(value = "tasks", allEntries = true)
    public Optional<Task> updateTask(Long Id, Task updatedTask) {
        return taskRepository.findById(Id).map(task -> {
            task.setDeadline(updatedTask.getDeadline());
            task.setDescription(updatedTask.getDescription());
            task.setStatus(updatedTask.getStatus());
            task.setPriority(updatedTask.getPriority());
            task.setTitle(updatedTask.getTitle());
            task.setAssignedUser(updatedTask.getAssignedUser());
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
