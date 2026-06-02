package ru.studyplanner.control;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.studyplanner.entity.AssignmentPriority;
import ru.studyplanner.entity.AssignmentStatus;
import ru.studyplanner.entity.CurrentUser;
import ru.studyplanner.mediator.AssignmentService;
import ru.studyplanner.mediator.dto.AssignmentCreateRequest;
import ru.studyplanner.mediator.dto.AssignmentFilter;
import ru.studyplanner.mediator.dto.AssignmentResponse;
import ru.studyplanner.mediator.dto.AssignmentUpdateRequest;
import ru.studyplanner.mediator.dto.StatusChangeRequest;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    public List<AssignmentResponse> findAll(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestParam(required = false) AssignmentStatus status,
            @RequestParam(required = false) AssignmentPriority priority,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dueFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dueTo,
            @RequestParam(required = false) String query
    ) {
        AssignmentFilter filter = new AssignmentFilter(status, priority, courseId, dueFrom, dueTo, query);
        return assignmentService.getAssignments(currentUser.id(), filter);
    }

    @GetMapping("/search")
    public List<AssignmentResponse> search(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestParam(required = false) AssignmentStatus status,
            @RequestParam(required = false) AssignmentPriority priority,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dueFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dueTo,
            @RequestParam(required = false) String query
    ) {
        AssignmentFilter filter = new AssignmentFilter(status, priority, courseId, dueFrom, dueTo, query);
        return assignmentService.getAssignments(currentUser.id(), filter);
    }

    @GetMapping("/{id}")
    public AssignmentResponse findById(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long id
    ) {
        return assignmentService.getAssignmentById(currentUser.id(), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssignmentResponse create(
            @AuthenticationPrincipal CurrentUser currentUser,
            @Valid @RequestBody AssignmentCreateRequest request
    ) {
        return assignmentService.createAssignment(currentUser.id(), request);
    }

    @PutMapping("/{id}")
    public AssignmentResponse update(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long id,
            @Valid @RequestBody AssignmentUpdateRequest request
    ) {
        return assignmentService.updateAssignment(currentUser.id(), id, request);
    }

    @PatchMapping("/{id}/status")
    public AssignmentResponse changeStatus(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long id,
            @Valid @RequestBody StatusChangeRequest request
    ) {
        return assignmentService.changeStatus(currentUser.id(), id, request.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long id
    ) {
        assignmentService.deleteAssignment(currentUser.id(), id);
    }
}

