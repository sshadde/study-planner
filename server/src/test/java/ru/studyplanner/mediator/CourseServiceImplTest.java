package ru.studyplanner.mediator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.studyplanner.entity.Course;
import ru.studyplanner.entity.User;
import ru.studyplanner.entity.UserRole;
import ru.studyplanner.foundation.AssignmentRepository;
import ru.studyplanner.foundation.CourseRepository;
import ru.studyplanner.foundation.UserRepository;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    void deleteCourseRejectsCourseWithAssignments() {
        Course course = new Course(
                new User("student@example.com", "hash", UserRole.STUDENT),
                "Databases",
                null,
                (short) 6,
                null
        );
        when(courseRepository.findByIdAndUserId(20L, 1L)).thenReturn(Optional.of(course));
        when(assignmentRepository.existsByCourseId(20L)).thenReturn(true);

        assertThatThrownBy(() -> courseService.deleteCourse(1L, 20L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("cannot be deleted");
        verify(courseRepository, never()).delete(course);
    }
}
