package ru.studyplanner.mediator;

import java.util.List;
import ru.studyplanner.entity.AssignmentStatus;
import ru.studyplanner.mediator.dto.AssignmentCreateRequest;
import ru.studyplanner.mediator.dto.AssignmentFilter;
import ru.studyplanner.mediator.dto.AssignmentResponse;
import ru.studyplanner.mediator.dto.AssignmentUpdateRequest;

public interface AssignmentService {

    List<AssignmentResponse> getAssignments(Long userId, AssignmentFilter filter);

    AssignmentResponse getAssignmentById(Long userId, Long assignmentId);

    AssignmentResponse createAssignment(Long userId, AssignmentCreateRequest request);

    AssignmentResponse updateAssignment(Long userId, Long assignmentId, AssignmentUpdateRequest request);

    void deleteAssignment(Long userId, Long assignmentId);

    AssignmentResponse changeStatus(Long userId, Long assignmentId, AssignmentStatus status);
}
