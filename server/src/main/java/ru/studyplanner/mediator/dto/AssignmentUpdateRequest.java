package ru.studyplanner.mediator.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import ru.studyplanner.entity.AssignmentPriority;

public record AssignmentUpdateRequest(
        @NotNull Long courseId,
        @NotBlank @Size(max = 200) String title,
        String description,
        @NotNull @FutureOrPresent Instant dueAt,
        AssignmentPriority priority
) {
}
