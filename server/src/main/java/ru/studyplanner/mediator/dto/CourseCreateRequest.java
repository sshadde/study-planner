package ru.studyplanner.mediator.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseCreateRequest(
        @NotBlank @Size(max = 160) String title,
        @Size(max = 160) String teacherName,
        @Min(1) @Max(10) Short semester,
        @Size(max = 16) String color
) {
}
