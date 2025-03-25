package com.example.demo.service;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.entity.TaskNotification;
import com.example.demo.entity.UserNotification;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendNotification(String routingKey, Object notification) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, routingKey, notification);
        System.out.println("📤 Sent notification to RabbitMQ: " + notification);
    }

    public void sendEmailNotification(TaskNotification taskNotification) {
        sendNotification(RabbitMQConfig.TASK_KEY, taskNotification);
    }

    public void sendEmailNotification(UserNotification userNotification) {
        sendNotification(RabbitMQConfig.USER_KEY, userNotification);
    }
}
