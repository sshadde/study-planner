package ru.studyplanner.mediator;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.studyplanner.entity.Assignment;
import ru.studyplanner.entity.AssignmentStatus;
import ru.studyplanner.entity.Course;
import ru.studyplanner.entity.User;
import ru.studyplanner.foundation.AssignmentRepository;
import ru.studyplanner.foundation.AssignmentSpecification;
import ru.studyplanner.foundation.CourseRepository;
import ru.studyplanner.foundation.IdentityMap;
import ru.studyplanner.foundation.UserRepository;
import ru.studyplanner.mediator.dto.AssignmentCreateRequest;
import ru.studyplanner.mediator.dto.AssignmentFilter;
import ru.studyplanner.mediator.dto.AssignmentResponse;
import ru.studyplanner.mediator.dto.AssignmentUpdateRequest;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final AssignmentMapper assignmentMapper;

    public AssignmentServiceImpl(
            AssignmentRepository assignmentRepository,
            CourseRepository courseRepository,
            UserRepository userRepository,
            AssignmentMapper assignmentMapper
    ) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.assignmentMapper = assignmentMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignments(Long userId, AssignmentFilter filter) {
        AssignmentFilter safeFilter = filter == null ? new AssignmentFilter(null, null, null, null, null, null) : filter;
        Specification<Assignment> specification = AssignmentSpecification.byFilter(
                userId,
                safeFilter.status(),
                safeFilter.priority(),
                safeFilter.courseId(),
                safeFilter.dueFrom(),
                safeFilter.dueTo(),
                safeFilter.query()
        );
        IdentityMap<Long, AssignmentResponse> responseIdentityMap = new IdentityMap<>();
        assignmentRepository.findAll(specification)
                .forEach(assignment -> responseIdentityMap.getOrPut(assignment.getId(), id -> assignmentMapper.toResponse(assignment)));
        return responseIdentityMap.values()
                .stream()
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AssignmentResponse getAssignmentById(Long userId, Long assignmentId) {
        return assignmentMapper.toResponse(loadAssignment(userId, assignmentId));
    }

    @Override
    @Transactional
    public AssignmentResponse createAssignment(Long userId, AssignmentCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Course course = loadCourse(userId, request.courseId());
        Assignment assignment = new Assignment(
                user,
                course,
                request.title(),
                request.description(),
                request.dueAt(),
                request.priority(),
                request.status()
        );
        return assignmentMapper.toResponse(assignmentRepository.save(assignment));
    }

    @Override
    @Transactional
    public AssignmentResponse updateAssignment(Long userId, Long assignmentId, AssignmentUpdateRequest request) {
        Assignment assignment = loadAssignment(userId, assignmentId);
        Course course = loadCourse(userId, request.courseId());
        assignment.update(course, request.title(), request.description(), request.dueAt(), request.priority());
        return assignmentMapper.toResponse(assignment);
    }

    @Override
    @Transactional
    public void deleteAssignment(Long userId, Long assignmentId) {
        Assignment assignment = loadAssignment(userId, assignmentId);
        assignmentRepository.delete(assignment);
    }

    @Override
    @Transactional
    public AssignmentResponse changeStatus(Long userId, Long assignmentId, AssignmentStatus status) {
        Assignment assignment = loadAssignment(userId, assignmentId);
        assignment.changeStatus(status);
        return assignmentMapper.toResponse(assignment);
    }

    private Assignment loadAssignment(Long userId, Long assignmentId) {
        return assignmentRepository.findByIdAndUserId(assignmentId, userId)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));
    }

    private Course loadCourse(Long userId, Long courseId) {
        return courseRepository.findByIdAndUserId(courseId, userId)
                .orElseThrow(() -> new NotFoundException("Course not found"));
    }
}
