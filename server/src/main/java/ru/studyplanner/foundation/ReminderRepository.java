package ru.studyplanner.foundation;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.studyplanner.entity.Reminder;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    Optional<Reminder> findByIdAndAssignmentUserId(Long id, Long userId);

    List<Reminder> findAllByAssignmentIdAndAssignmentUserIdOrderByRemindAtAsc(Long assignmentId, Long userId);
}
