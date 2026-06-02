package ru.studyplanner.mediator.dto;

import java.time.Instant;
import ru.studyplanner.entity.AssignmentPriority;
import ru.studyplanner.entity.AssignmentStatus;

public record AssignmentFilter(
        AssignmentStatus status,
        AssignmentPriority priority,
        Long courseId,
        Instant dueFrom,
        Instant dueTo,
        String query
) {
}
