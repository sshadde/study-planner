package ru.studyplanner.mediator;

import java.util.List;
import ru.studyplanner.mediator.dto.CourseCreateRequest;
import ru.studyplanner.mediator.dto.CourseResponse;
import ru.studyplanner.mediator.dto.CourseUpdateRequest;

public interface CourseService {

    List<CourseResponse> getCourses(Long userId);

    CourseResponse createCourse(Long userId, CourseCreateRequest request);

    CourseResponse updateCourse(Long userId, Long courseId, CourseUpdateRequest request);

    void deleteCourse(Long userId, Long courseId);
}
