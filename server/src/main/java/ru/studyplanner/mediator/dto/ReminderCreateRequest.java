package ru.studyplanner.mediator.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record ReminderCreateRequest(
        @NotNull @FutureOrPresent Instant remindAt,
        @Size(max = 500) String message
) {
}
