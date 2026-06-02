package ru.studyplanner.mediator;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.studyplanner.entity.Course;
import ru.studyplanner.entity.User;
import ru.studyplanner.foundation.AssignmentRepository;
import ru.studyplanner.foundation.CourseRepository;
import ru.studyplanner.foundation.UserRepository;
import ru.studyplanner.mediator.dto.CourseCreateRequest;
import ru.studyplanner.mediator.dto.CourseResponse;
import ru.studyplanner.mediator.dto.CourseUpdateRequest;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final CourseMapper courseMapper;

    public CourseServiceImpl(
            CourseRepository courseRepository,
            AssignmentRepository assignmentRepository,
            UserRepository userRepository,
            CourseMapper courseMapper
    ) {
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.courseMapper = courseMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getCourses(Long userId) {
        return courseRepository.findAllByUserIdOrderByTitleAsc(userId)
                .stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CourseResponse createCourse(Long userId, CourseCreateRequest request) {
        if (courseRepository.existsByUserIdAndTitleIgnoreCase(userId, request.title())) {
            throw new ConflictException("Course with this title already exists");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Course course = new Course(user, request.title(), request.teacherName(), request.semester(), request.color());
        return courseMapper.toResponse(courseRepository.save(course));
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long userId, Long courseId, CourseUpdateRequest request) {
        Course course = courseRepository.findByIdAndUserId(courseId, userId)
                .orElseThrow(() -> new NotFoundException("Course not found"));
        course.update(request.title(), request.teacherName(), request.semester(), request.color());
        return courseMapper.toResponse(course);
    }

    @Override
    @Transactional
    public void deleteCourse(Long userId, Long courseId) {
        Course course = courseRepository.findByIdAndUserId(courseId, userId)
                .orElseThrow(() -> new NotFoundException("Course not found"));
        if (assignmentRepository.existsByCourseId(courseId)) {
            throw new BusinessRuleException("Course has assignments and cannot be deleted");
        }
        courseRepository.delete(course);
    }
}
