package com.example.demo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskNotification {

    private Long taskId;
    private Long AssignedUserId;
    private String message;
    private String userEmail;
    private Notification.NotificationType type;

    public TaskNotification(Long taskId, Long AssignedUserId, String message, String userEmail, Notification.NotificationType type  ) {
        this.taskId = taskId;
        this.AssignedUserId = AssignedUserId;
        this.message = message;
        this.userEmail = userEmail;
        this.type = type;
    }
}
