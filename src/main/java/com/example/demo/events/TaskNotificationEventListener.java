package com.example.demo.events;

import com.example.demo.entity.Notification;
import com.example.demo.entity.Task;
import com.example.demo.entity.TaskAssignment;
import com.example.demo.entity.User;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.TaskAssignmentRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class TaskNotificationEventListener {
    @Autowired
    private UserService userService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    @EventListener
    public void handleTaskNotification(TaskNotificationEvent event) {
        Notification notification = new Notification();
        notification.setMessage(event.getMessage());
        notification.setType(event.getType());
        User user = userService.findUserById(event.getAssignedUserId());
        notification.setRecipient(user);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setStatus(Notification.NotificationStatus.PENDING);
        notificationRepository.save(notification);
        System.out.println(Notification.NotificationStatus.valueOf(Notification.NotificationStatus.FAILED.toString())+" "+Notification.NotificationType.TASK_ASSIGNED);
        try{
            emailService.sendMail(event.getUserEmail(),Notification.NotificationType.TASK_ASSIGNED.toString(), event.getMessage());
            notification.setStatus(Notification.NotificationStatus.SENT);
            System.out.println("Hello");
        } catch (Exception e) {
            System.out.println(Notification.NotificationStatus.FAILED);
            notification.setStatus(Notification.NotificationStatus.FAILED);
        }
        notificationRepository.save(notification);
    }
}
