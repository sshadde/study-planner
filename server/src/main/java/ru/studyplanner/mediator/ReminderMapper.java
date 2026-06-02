package ru.studyplanner.mediator;

import org.springframework.stereotype.Component;
import ru.studyplanner.entity.Reminder;
import ru.studyplanner.mediator.dto.ReminderResponse;

@Component
public class ReminderMapper {

    public ReminderResponse toResponse(Reminder reminder) {
        return new ReminderResponse(
                reminder.getId(),
                reminder.getAssignment().getId(),
                reminder.getRemindAt(),
                reminder.getMessage(),
                reminder.isEnabled(),
                reminder.getSentAt(),
                reminder.getCreatedAt()
        );
    }
}
