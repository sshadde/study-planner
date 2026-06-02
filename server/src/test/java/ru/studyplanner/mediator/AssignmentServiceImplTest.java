package ru.studyplanner.mediator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import ru.studyplanner.entity.Assignment;
import ru.studyplanner.entity.AssignmentPriority;
import ru.studyplanner.entity.AssignmentStatus;
import ru.studyplanner.entity.Course;
import ru.studyplanner.entity.User;
import ru.studyplanner.entity.UserRole;
import ru.studyplanner.foundation.AssignmentRepository;
import ru.studyplanner.foundation.CourseRepository;
import ru.studyplanner.foundation.UserRepository;
import ru.studyplanner.mediator.dto.AssignmentCreateRequest;
import ru.studyplanner.mediator.dto.AssignmentFilter;
import ru.studyplanner.mediator.dto.AssignmentUpdateRequest;

class AssignmentServiceImplTest {

    private AssignmentRepository assignmentRepository;
    private CourseRepository courseRepository;
    private UserRepository userRepository;
    private AssignmentServiceImpl assignmentService;

    @BeforeEach
    void setUp() {
        assignmentRepository = org.mockito.Mockito.mock(AssignmentRepository.class);
        courseRepository = org.mockito.Mockito.mock(CourseRepository.class);
        userRepository = org.mockito.Mockito.mock(UserRepository.class);
        assignmentService = new AssignmentServiceImpl(
                assignmentRepository,
                courseRepository,
                userRepository,
                new AssignmentMapper()
        );
    }

    @Test
    void getAssignmentsAppliesFilterAndMapsResult() {
        Assignment assignment = assignment();
        when(assignmentRepository.findAll(any(Specification.class))).thenReturn(List.of(assignment));

        var result = assignmentService.getAssignments(
                1L,
                new AssignmentFilter(AssignmentStatus.NEW, null, null, null, null, "project")
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Course project");
    }

    @Test
    void createAssignmentChecksCourseOwnershipAndSavesEntity() {
        User user = user();
        Course course = course(user);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(courseRepository.findByIdAndUserId(2L, 1L)).thenReturn(Optional.of(course));
        when(assignmentRepository.save(any(Assignment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = assignmentService.createAssignment(
                1L,
                new AssignmentCreateRequest(
                        2L,
                        "Course project",
                        "Prepare backend",
                        Instant.now().plus(3, ChronoUnit.DAYS),
                        AssignmentPriority.HIGH,
                        AssignmentStatus.NEW
                )
        );

        assertThat(response.title()).isEqualTo("Course project");
        verify(assignmentRepository).save(any(Assignment.class));
    }

    @Test
    void changeStatusUsesDomainMethod() {
        Assignment assignment = assignment();
        when(assignmentRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(assignment));

        var response = assignmentService.changeStatus(1L, 10L, AssignmentStatus.DONE);

        assertThat(response.status()).isEqualTo(AssignmentStatus.DONE);
        assertThat(response.completedAt()).isNotNull();
    }

    @Test
    void updateAssignmentChangesEditableFields() {
        User user = user();
        Assignment assignment = assignment();
        Course newCourse = new Course(user, "Databases", null, (short) 6, null);
        Instant newDueAt = Instant.now().plus(4, ChronoUnit.DAYS);
        when(assignmentRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(assignment));
        when(courseRepository.findByIdAndUserId(3L, 1L)).thenReturn(Optional.of(newCourse));

        var response = assignmentService.updateAssignment(
                1L,
                10L,
                new AssignmentUpdateRequest(
                        3L,
                        "Updated title",
                        "Updated description",
                        newDueAt,
                        AssignmentPriority.LOW
                )
        );

        assertThat(response.title()).isEqualTo("Updated title");
        assertThat(response.priority()).isEqualTo(AssignmentPriority.LOW);
    }

    private static Assignment assignment() {
        User user = user();
        return new Assignment(
                user,
                course(user),
                "Course project",
                "Prepare backend",
                Instant.now().plus(2, ChronoUnit.DAYS),
                AssignmentPriority.HIGH,
                AssignmentStatus.NEW
        );
    }

    private static Course course(User user) {
        return new Course(user, "Software Engineering", null, (short) 6, null);
    }

    private static User user() {
        return new User("student@example.com", "hash", UserRole.STUDENT);
    }
}
