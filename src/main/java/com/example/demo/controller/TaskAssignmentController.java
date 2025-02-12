package com.example.demo.controller;

import com.example.demo.dto.TaskAssignmentDTO;
import com.example.demo.dtomapper.DTOMapper;
import com.example.demo.entity.TaskAssignment;
import com.example.demo.events.TaskAssignmentUpdatedEvent;
import com.example.demo.service.TaskAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks/{taskId}/assignments")
public class TaskAssignmentController {

    @Autowired
    private TaskAssignmentService taskAssignmentService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private DTOMapper dtoMapper;

    @PreAuthorize("hasAuthority('TASK_ASSIGN')")
    @PostMapping("/assign")
    public ResponseEntity<String> assignUserToTask(@PathVariable Long taskId, @RequestBody TaskAssignmentDTO taskAssignmentDTO) {

        Principal principal;
        taskAssignmentService.assignUserToTask(
                taskId,
                taskAssignmentDTO.getUser().getId(),
                taskAssignmentDTO.getAssignmentDate(),
                taskAssignmentDTO.getStatus()
        );

        return ResponseEntity.status(HttpStatus.OK).body("Task successfully assigned to user");
    }
    @PreAuthorize("hasAuthority('TASK_ASSIGN')")
    @GetMapping
    public List<TaskAssignmentDTO> getAssignmentsForTask(@PathVariable Long taskId) {
        List<TaskAssignment> taskAssignments = taskAssignmentService.getAssignmentsForTask(taskId);
        return dtoMapper.mapToTaskAssignmentDTOList(taskAssignments);
    }

    @PreAuthorize("hasAuthority('TASK_UPDATE_OWN')")
    @PutMapping("/{assignmentId}")
    public ResponseEntity<?> updateAssignmentStatus(@PathVariable Long assignmentId, @RequestBody Map<String, String> payload) {
        String status = payload.get("status");
        TaskAssignmentDTO taskAssignmentDTO = taskAssignmentService.updateAssignmentStatus(assignmentId, status);

        //Publish or trigger the event
        applicationEventPublisher.publishEvent(new TaskAssignmentUpdatedEvent(this, assignmentId));

        return ResponseEntity.status(HttpStatus.OK).body(taskAssignmentDTO);
    }

    @PreAuthorize("hasAuthority('TASK_ASSIGN')")
    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<?> deleteAssignmentStatus(@PathVariable Long assignmentId) {
        taskAssignmentService.unassignUserFromTask(assignmentId);
        return ResponseEntity.noContent().build();
    }
}
