package ru.studyplanner.mediator;

import org.springframework.stereotype.Component;
import ru.studyplanner.entity.Course;
import ru.studyplanner.mediator.dto.CourseResponse;

@Component
public class CourseMapper {

    public CourseResponse toResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getTeacherName(),
                course.getSemester(),
                course.getColor(),
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}
