package com.example.demo.events;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.entity.Notification;
import com.example.demo.entity.TaskNotification;
import com.example.demo.entity.User;
import com.example.demo.entity.UserNotification;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class UserNotificationEventListener {
    @Autowired
    private UserService userService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE)
    public void handleUserNotification(UserNotification userNotification) {

        Notification notification = new Notification();
        notification.setMessage(userNotification.getMessage());
        notification.setType(userNotification.getType());

        User user = userService.findByUsername(userNotification.getUserEmail());
        notification.setRecipient(user);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setStatus(Notification.NotificationStatus.PENDING);
        notificationRepository.save(notification);
        try{
            emailService.sendMail(userNotification.getUserEmail(),userNotification.getType().toString(), userNotification.getMessage());
            notification.setStatus(Notification.NotificationStatus.SENT);
            System.out.println("✅ Email sent to: " + userNotification.getUserEmail());
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            notification.setStatus(Notification.NotificationStatus.FAILED);
        }
        notificationRepository.save(notification);
    }
}
