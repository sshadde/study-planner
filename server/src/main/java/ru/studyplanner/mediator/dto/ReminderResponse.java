package ru.studyplanner.mediator.dto;

import java.time.Instant;

public record ReminderResponse(
        Long id,
        Long assignmentId,
        Instant remindAt,
        String message,
        boolean enabled,
        Instant sentAt,
        Instant createdAt
) {
}
