package com.example.demo.events;

import com.example.demo.entity.Notification;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TaskNotificationEvent extends ApplicationEvent {

    private final Long taskId;
    private final Long AssignedUserId;
    private final String message;
    private final String userEmail;
    private final Notification.NotificationType type;

    public TaskNotificationEvent(Object source,Long taskId,Long AssignedUserId,String message, String userEmail, Notification.NotificationType type  ) {
        super(source);
        this.taskId = taskId;
        this.AssignedUserId = AssignedUserId;
        this.message = message;
        this.userEmail = userEmail;
        this.type = type;
    }
}
