package com.example.demo.events;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.entity.*;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
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
    @RabbitListener(queues = RabbitMQConfig.TASK_QUEUE)
    public void handleTaskNotification(TaskNotification taskNotification) {

        Notification notification = new Notification();
        notification.setMessage(taskNotification.getMessage());
        notification.setType(taskNotification.getType());

        User user = userService.findUserById(taskNotification.getAssignedUserId());
        notification.setRecipient(user);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setStatus(Notification.NotificationStatus.PENDING);
        notificationRepository.save(notification);
        try{
            emailService.sendMail(taskNotification.getUserEmail(),taskNotification.getType().toString(), taskNotification.getMessage());
            notification.setStatus(Notification.NotificationStatus.SENT);
            System.out.println("✅ Email sent to: " + taskNotification.getUserEmail());
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            notification.setStatus(Notification.NotificationStatus.FAILED);
        }
        notificationRepository.save(notification);
    }
}
