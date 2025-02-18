package com.example.demo.service;

import com.example.demo.dto.TaskAssignmentDTO;
import com.example.demo.dtomapper.DTOMapper;
import com.example.demo.entity.Task;
import com.example.demo.entity.TaskAssignment;
import com.example.demo.entity.User;
import com.example.demo.events.TaskAssignmentUpdatedEvent;
import com.example.demo.exceptions.AccessDeniedException;
import com.example.demo.exceptions.TaskAssignmentNotFoundException;
import com.example.demo.exceptions.TaskNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.repository.TaskAssignmentRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class TaskAssignmentService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskAssignmentRepository taskAssignmentRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private DTOMapper dtoMapper;

    @CacheEvict(value = "tasks", allEntries = true)
    public TaskAssignment assignUserToTask(Long taskId, Long userId, LocalDate assignedDate, String status) {

        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Cannot find the task"));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Cannot find the user"));

        TaskAssignment taskAssignment = new TaskAssignment();
        taskAssignment.setTask(task);
        taskAssignment.setUser(user);
        taskAssignment.setAssignedDate(assignedDate);
        taskAssignment.setStatus(status);
        taskAssignment = taskAssignmentRepository.save(taskAssignment);
        eventPublisher.publishEvent(new TaskAssignmentUpdatedEvent(this,taskAssignment.getId()));

        return taskAssignment;
    }

    public List<TaskAssignment> getAssignmentsForTask(Long taskId) {

        return taskAssignmentRepository.findByTaskId(taskId);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public TaskAssignmentDTO updateAssignmentStatus(Long assignmentId, String status, User currentUser) {

        TaskAssignment taskAssignment = taskAssignmentRepository.findById(assignmentId).orElseThrow(()->
                    new RuntimeException("Cannot Find the corresponding task assignment")
        );
        boolean canUpdateAnyTask = userService.canPerformAny(currentUser,"TASK_UPDATE_ALL");
        boolean isAssigned = taskAssignment.getUser().getId().equals(currentUser.getId());

        if(!canUpdateAnyTask && !isAssigned) {
            throw new AccessDeniedException("You do not have permissions to update the task assignment status");
        }

        taskAssignment.setStatus(status);
        taskAssignmentRepository.save(taskAssignment);

        return dtoMapper.mapToTaskAssignmentDTO(taskAssignment);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public void unassignUserFromTask(Long assignmentId) {

        TaskAssignment taskAssignment = taskAssignmentRepository.findById(assignmentId).orElseThrow(()-> new TaskAssignmentNotFoundException("Unable to find the corresponding assignment"));
        taskAssignmentRepository.deleteById(taskAssignment.getId());

        return;
    }
}
