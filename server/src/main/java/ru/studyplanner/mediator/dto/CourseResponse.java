package ru.studyplanner.mediator.dto;

import java.time.Instant;

public record CourseResponse(
        Long id,
        String title,
        String teacherName,
        Short semester,
        String color,
        Instant createdAt,
        Instant updatedAt
) {
}
