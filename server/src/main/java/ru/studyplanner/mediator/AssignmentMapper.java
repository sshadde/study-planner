package ru.studyplanner.mediator;

import org.springframework.stereotype.Component;
import ru.studyplanner.entity.Assignment;
import ru.studyplanner.mediator.dto.AssignmentResponse;

@Component
public class AssignmentMapper {

    public AssignmentResponse toResponse(Assignment assignment) {
        return new AssignmentResponse(
                assignment.getId(),
                assignment.getCourse().getId(),
                assignment.getCourse().getTitle(),
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getDueAt(),
                assignment.getPriority(),
                assignment.getStatus(),
                assignment.getCreatedAt(),
                assignment.getUpdatedAt(),
                assignment.getCompletedAt()
        );
    }
}
