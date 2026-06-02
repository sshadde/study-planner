package ru.studyplanner.mediator.dto;

import java.time.Instant;
import ru.studyplanner.entity.UserRole;

public record AdminUserResponse(
        Long id,
        String email,
        UserRole role,
        boolean enabled,
        String fullName,
        String groupName,
        Instant createdAt
) {
}
