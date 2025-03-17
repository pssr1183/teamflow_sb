package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User recipient;
    private NotificationStatus status;
    private NotificationType type;
    private LocalDateTime createdAt;

    public enum NotificationStatus {
        PENDING, SENT, FAILED
    }

    public enum NotificationType {
        TASK_ASSIGNED
    }
}
