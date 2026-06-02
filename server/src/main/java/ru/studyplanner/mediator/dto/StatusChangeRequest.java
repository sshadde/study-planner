package ru.studyplanner.mediator.dto;

import jakarta.validation.constraints.NotNull;
import ru.studyplanner.entity.AssignmentStatus;

public record StatusChangeRequest(@NotNull AssignmentStatus status) {
}
