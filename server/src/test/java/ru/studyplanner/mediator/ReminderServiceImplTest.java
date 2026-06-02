package ru.studyplanner.mediator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.studyplanner.entity.Assignment;
import ru.studyplanner.entity.AssignmentPriority;
import ru.studyplanner.entity.AssignmentStatus;
import ru.studyplanner.entity.Course;
import ru.studyplanner.entity.User;
import ru.studyplanner.entity.UserRole;
import ru.studyplanner.foundation.AssignmentRepository;
import ru.studyplanner.foundation.ReminderRepository;
import ru.studyplanner.mediator.dto.ReminderCreateRequest;

@ExtendWith(MockitoExtension.class)
class ReminderServiceImplTest {

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private ReminderMapper reminderMapper;

    @InjectMocks
    private ReminderServiceImpl reminderService;

    @Test
    void createReminderRejectsDateAfterAssignmentDueDate() {
        Assignment assignment = assignment(Instant.now().plus(1, ChronoUnit.DAYS));
        when(assignmentRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(assignment));

        ReminderCreateRequest request = new ReminderCreateRequest(
                Instant.now().plus(2, ChronoUnit.DAYS),
                "Too late"
        );

        assertThatThrownBy(() -> reminderService.createReminder(1L, 10L, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("cannot be later");
        verify(reminderRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private static Assignment assignment(Instant dueAt) {
        User user = new User("student@example.com", "hash", UserRole.STUDENT);
        Course course = new Course(user, "Software Engineering", null, (short) 6, null);
        return new Assignment(
                user,
                course,
                "Course project",
                null,
                dueAt,
                AssignmentPriority.HIGH,
                AssignmentStatus.NEW
        );
    }
}
