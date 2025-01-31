package com.example.demo.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TaskAssignmentUpdatedEvent extends ApplicationEvent {
    private final Long assignmentId;

    public TaskAssignmentUpdatedEvent(Object source, Long assignmentId) {
        super(source);
        this.assignmentId = assignmentId;
    }
}