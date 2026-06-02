package ru.studyplanner.mediator.dto;

import ru.studyplanner.entity.UserRole;

public record CurrentUserResponse(
        Long id,
        String email,
        UserRole role,
        String fullName,
        String groupName
) {
}
