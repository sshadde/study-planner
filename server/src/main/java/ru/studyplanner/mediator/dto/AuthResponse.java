package ru.studyplanner.mediator.dto;

import ru.studyplanner.entity.UserRole;

public record AuthResponse(
        String accessToken,
        Long userId,
        String email,
        UserRole role,
        String fullName,
        String groupName
) {
}
