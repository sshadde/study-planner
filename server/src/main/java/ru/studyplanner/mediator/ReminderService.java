package ru.studyplanner.mediator;

import java.util.List;
import ru.studyplanner.mediator.dto.ReminderCreateRequest;
import ru.studyplanner.mediator.dto.ReminderResponse;

public interface ReminderService {

    ReminderResponse createReminder(Long userId, Long assignmentId, ReminderCreateRequest request);

    List<ReminderResponse> getAssignmentReminders(Long userId, Long assignmentId);

    void deleteReminder(Long userId, Long reminderId);
}
