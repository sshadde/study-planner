package ru.studyplanner.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

class AssignmentTest {

    @Test
    void changeStatusToDoneSetsCompletedAt() {
        Assignment assignment = assignment(Instant.now().plus(2, ChronoUnit.DAYS));

        assignment.changeStatus(AssignmentStatus.DONE);

        assertThat(assignment.getStatus()).isEqualTo(AssignmentStatus.DONE);
        assertThat(assignment.getCompletedAt()).isNotNull();
    }

    @Test
    void changeStatusFromDoneClearsCompletedAt() {
        Assignment assignment = assignment(Instant.now().plus(2, ChronoUnit.DAYS));
        assignment.changeStatus(AssignmentStatus.DONE);

        assignment.changeStatus(AssignmentStatus.IN_PROGRESS);

        assertThat(assignment.getCompletedAt()).isNull();
    }

    @Test
    void isOverdueIgnoresDoneAssignments() {
        Assignment assignment = assignment(Instant.now().minus(1, ChronoUnit.DAYS));
        assignment.changeStatus(AssignmentStatus.DONE);

        assertThat(assignment.isOverdue(Instant.now())).isFalse();
    }

    @Test
    void emptyTitleIsRejected() {
        User user = new User("student@example.com", "hash", UserRole.STUDENT);
        Course course = new Course(user, "Math", null, (short) 6, null);

        assertThatThrownBy(() -> new Assignment(
                user,
                course,
                " ",
                null,
                Instant.now().plus(1, ChronoUnit.DAYS),
                AssignmentPriority.MEDIUM,
                AssignmentStatus.NEW
        )).isInstanceOf(IllegalArgumentException.class);
    }

    private static Assignment assignment(Instant dueAt) {
        User user = new User("student@example.com", "hash", UserRole.STUDENT);
        Course course = new Course(user, "Software Engineering", null, (short) 6, "#3366ff");
        return new Assignment(
                user,
                course,
                "Course project",
                "Prepare backend",
                dueAt,
                AssignmentPriority.HIGH,
                AssignmentStatus.NEW
        );
    }
}
