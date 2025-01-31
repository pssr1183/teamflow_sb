package com.example.demo.service;

import com.example.demo.dto.TaskAssignmentDTO;
import com.example.demo.dtomapper.DTOMapper;
import com.example.demo.entity.Task;
import com.example.demo.entity.TaskAssignment;
import com.example.demo.entity.User;
import com.example.demo.events.TaskAssignmentUpdatedEvent;
import com.example.demo.exceptions.TaskNotFoundException;
import com.example.demo.repository.TaskAssignmentRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskAssignmentService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskAssignmentRepository taskAssignmentRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private DTOMapper dtoMapper;

    public TaskAssignment assignUserToTask(Long taskId, Long userId, LocalDate assignedDate, String status) {

        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Cannot find the task"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Cannot find the user"));

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

    public TaskAssignmentDTO updateAssignmentStatus(Long assignmentId, String status) {
        TaskAssignment taskAssignment = taskAssignmentRepository.findById(assignmentId).orElseThrow(()->
                    new RuntimeException("Cannot Find the corresponding task assignment")
        );
        taskAssignment.setStatus(status);
        taskAssignmentRepository.save(taskAssignment);
        return dtoMapper.mapToTaskAssignmentDTO(taskAssignment);
    }
    public void unassignUserFromTask(Long assignmentId) {
        TaskAssignment taskAssignment = taskAssignmentRepository.findById(assignmentId).orElseThrow(()-> new RuntimeException("Unable to find the corresponding assignment"));
        taskAssignmentRepository.deleteById(taskAssignment.getId());
    }
}
