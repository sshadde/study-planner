package ru.studyplanner.mediator.dto;

import java.time.Instant;
import ru.studyplanner.entity.AssignmentPriority;
import ru.studyplanner.entity.AssignmentStatus;

public record AssignmentResponse(
        Long id,
        Long courseId,
        String courseTitle,
        String title,
        String description,
        Instant dueAt,
        AssignmentPriority priority,
        AssignmentStatus status,
        Instant createdAt,
        Instant updatedAt,
        Instant completedAt
) {
}
