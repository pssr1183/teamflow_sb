package com.example.demo.dtomapper;

import com.example.demo.dto.TaskAssignmentDTO;
import com.example.demo.dto.TaskDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.Task;
import com.example.demo.entity.TaskAssignment;
import com.example.demo.entity.User;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component

public class DTOMapper {

    public TaskDTO mapToTaskDTO(Task task, boolean canViewAll, boolean isSingleTask, User currentUser) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setTitle(task.getTitle());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setDeadline(task.getDeadline()); // Assuming deadline is already a LocalDate
        taskDTO.setPriority(task.getPriority());
        taskDTO.setStatus(task.getStatus());

        // Map assignments to DTOs
        List<TaskAssignmentDTO> assignmentDTOS = task.getAssignments()
                .stream()
                .filter(assignment -> canViewAll || (assignment.getUser() != null && assignment.getUser().getId().equals(currentUser.getId())))
                .map(assignment -> {
                    TaskAssignmentDTO taskAssignmentDTO = new TaskAssignmentDTO();
                    if(canViewAll && !isSingleTask) {

                        User user = assignment.getUser();
                        if(user != null) {
                            UserDTO userDTO = new UserDTO();
                            userDTO.setId(user.getId());
                            userDTO.setUsername(user.getUsername());
                            userDTO.setRolenames(user.getRoles() != null ? user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()) : null);
                            taskAssignmentDTO.setUser(userDTO);
                        }
                    }
                    taskAssignmentDTO.setId(assignment.getId());
                    taskAssignmentDTO.setAssignmentDate(assignment.getAssignedDate());
                    taskAssignmentDTO.setStatus(assignment.getStatus());

                    return taskAssignmentDTO;
                })
                .collect(Collectors.toList());

        taskDTO.setAssignments(assignmentDTOS);

        return taskDTO;
    }

    public List<TaskDTO> mapToDTOList(List<Task> tasks, boolean canViewAllTasks, User user) {
        return tasks.stream()
                .map(task -> mapToTaskDTO(task,canViewAllTasks,false, user))
                .collect(Collectors.toList());
    }

    public TaskAssignmentDTO mapToTaskAssignmentDTO(TaskAssignment taskAssignment) {

        User user = taskAssignment.getUser();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setRolenames(user.getRoles() != null ? user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()) : null);

        TaskAssignmentDTO taskAssignmentDTO = new TaskAssignmentDTO();
        taskAssignmentDTO.setId(taskAssignment.getId());
        taskAssignmentDTO.setUser(userDTO);
        taskAssignmentDTO.setAssignmentDate(taskAssignment.getAssignedDate());
        taskAssignmentDTO.setStatus(taskAssignment.getStatus());
        return taskAssignmentDTO;
    }

    public List<TaskAssignmentDTO> mapToTaskAssignmentDTOList(List<TaskAssignment> taskAssignments) {
        return taskAssignments.stream()
                .map(this::mapToTaskAssignmentDTO)
                .collect(Collectors.toList());
    }
}