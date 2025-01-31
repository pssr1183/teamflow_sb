package com.example.demo.events;

import com.example.demo.entity.Task;
import com.example.demo.entity.TaskAssignment;
import com.example.demo.repository.TaskAssignmentRepository;
import com.example.demo.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TaskAssignmentUpdatedEventListener {
    @Autowired
    private TaskAssignmentRepository taskAssignmentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Transactional
    @EventListener
    public void handleTaskAssignmentUpdated(TaskAssignmentUpdatedEvent event) {
        TaskAssignment taskAssignment = taskAssignmentRepository.findById(event.getAssignmentId()).orElseThrow(
                ()-> new RuntimeException("No corresponding task assignment found")
        );
        Task task = taskAssignment.getTask();
        boolean allSubmitted = task.getAssignments().stream().allMatch(a -> "Submitted".equals(a.getStatus()));
        if(allSubmitted) {
            task.setStatus("Completed");
        } else {
           boolean anyInProgress = task.getAssignments().stream().anyMatch(a -> "In Progress".equals(a.getStatus()));
           if(anyInProgress) {
               task.setStatus("In Progress");
           }
           else task.setStatus("To Do");
        }
        taskRepository.save(task);

    }
}
