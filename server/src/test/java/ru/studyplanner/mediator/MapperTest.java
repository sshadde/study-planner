package ru.studyplanner.mediator;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import ru.studyplanner.entity.Assignment;
import ru.studyplanner.entity.AssignmentPriority;
import ru.studyplanner.entity.AssignmentStatus;
import ru.studyplanner.entity.Course;
import ru.studyplanner.entity.Reminder;
import ru.studyplanner.entity.User;
import ru.studyplanner.entity.UserRole;

class MapperTest {

    @Test
    void courseMapperConvertsEntityToResponse() {
        Course course = new Course(user(), "Databases", "Teacher", (short) 6, "#224466");

        var response = new CourseMapper().toResponse(course);

        assertThat(response.title()).isEqualTo("Databases");
        assertThat(response.teacherName()).isEqualTo("Teacher");
        assertThat(response.semester()).isEqualTo((short) 6);
    }

    @Test
    void assignmentMapperConvertsEntityToResponse() {
        Assignment assignment = assignment();

        var response = new AssignmentMapper().toResponse(assignment);

        assertThat(response.title()).isEqualTo("Course project");
        assertThat(response.priority()).isEqualTo(AssignmentPriority.HIGH);
        assertThat(response.status()).isEqualTo(AssignmentStatus.NEW);
    }

    @Test
    void reminderMapperConvertsEntityToResponse() {
        Reminder reminder = new Reminder(
                assignment(),
                Instant.now().plus(1, ChronoUnit.HOURS),
                "Start work"
        );

        var response = new ReminderMapper().toResponse(reminder);

        assertThat(response.message()).isEqualTo("Start work");
        assertThat(response.enabled()).isTrue();
    }

    private static Assignment assignment() {
        User user = user();
        Course course = new Course(user, "Software Engineering", null, (short) 6, null);
        return new Assignment(
                user,
                course,
                "Course project",
                "Prepare backend",
                Instant.now().plus(2, ChronoUnit.DAYS),
                AssignmentPriority.HIGH,
                AssignmentStatus.NEW
        );
    }

    private static User user() {
        return new User("student@example.com", "hash", UserRole.STUDENT);
    }
}
