package ru.studyplanner.control;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.studyplanner.entity.CurrentUser;
import ru.studyplanner.mediator.ReminderService;
import ru.studyplanner.mediator.dto.ReminderCreateRequest;
import ru.studyplanner.mediator.dto.ReminderResponse;

@RestController
@RequestMapping("/api")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @GetMapping("/assignments/{assignmentId}/reminders")
    public List<ReminderResponse> findAll(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long assignmentId
    ) {
        return reminderService.getAssignmentReminders(currentUser.id(), assignmentId);
    }

    @PostMapping("/assignments/{assignmentId}/reminders")
    @ResponseStatus(HttpStatus.CREATED)
    public ReminderResponse create(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long assignmentId,
            @Valid @RequestBody ReminderCreateRequest request
    ) {
        return reminderService.createReminder(currentUser.id(), assignmentId, request);
    }

    @DeleteMapping("/reminders/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long id
    ) {
        reminderService.deleteReminder(currentUser.id(), id);
    }
}

