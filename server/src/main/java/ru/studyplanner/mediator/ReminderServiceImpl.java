package ru.studyplanner.mediator;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.studyplanner.entity.Assignment;
import ru.studyplanner.entity.Reminder;
import ru.studyplanner.foundation.AssignmentRepository;
import ru.studyplanner.foundation.ReminderRepository;
import ru.studyplanner.mediator.dto.ReminderCreateRequest;
import ru.studyplanner.mediator.dto.ReminderResponse;

@Service
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final AssignmentRepository assignmentRepository;
    private final ReminderMapper reminderMapper;

    public ReminderServiceImpl(
            ReminderRepository reminderRepository,
            AssignmentRepository assignmentRepository,
            ReminderMapper reminderMapper
    ) {
        this.reminderRepository = reminderRepository;
        this.assignmentRepository = assignmentRepository;
        this.reminderMapper = reminderMapper;
    }

    @Override
    @Transactional
    public ReminderResponse createReminder(Long userId, Long assignmentId, ReminderCreateRequest request) {
        Assignment assignment = assignmentRepository.findByIdAndUserId(assignmentId, userId)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));
        if (request.remindAt().isAfter(assignment.getDueAt())) {
            throw new BusinessRuleException("Reminder cannot be later than assignment due date");
        }
        Reminder reminder = new Reminder(assignment, request.remindAt(), request.message());
        return reminderMapper.toResponse(reminderRepository.save(reminder));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReminderResponse> getAssignmentReminders(Long userId, Long assignmentId) {
        if (assignmentRepository.findByIdAndUserId(assignmentId, userId).isEmpty()) {
            throw new NotFoundException("Assignment not found");
        }
        return reminderRepository.findAllByAssignmentIdAndAssignmentUserIdOrderByRemindAtAsc(assignmentId, userId)
                .stream()
                .map(reminderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteReminder(Long userId, Long reminderId) {
        Reminder reminder = reminderRepository.findByIdAndAssignmentUserId(reminderId, userId)
                .orElseThrow(() -> new NotFoundException("Reminder not found"));
        reminderRepository.delete(reminder);
    }
}
