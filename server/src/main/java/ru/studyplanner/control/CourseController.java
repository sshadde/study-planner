package ru.studyplanner.control;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.studyplanner.entity.CurrentUser;
import ru.studyplanner.mediator.CourseService;
import ru.studyplanner.mediator.dto.CourseCreateRequest;
import ru.studyplanner.mediator.dto.CourseResponse;
import ru.studyplanner.mediator.dto.CourseUpdateRequest;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<CourseResponse> findAll(@AuthenticationPrincipal CurrentUser currentUser) {
        return courseService.getCourses(currentUser.id());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse create(
            @AuthenticationPrincipal CurrentUser currentUser,
            @Valid @RequestBody CourseCreateRequest request
    ) {
        return courseService.createCourse(currentUser.id(), request);
    }

    @PutMapping("/{id}")
    public CourseResponse update(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long id,
            @Valid @RequestBody CourseUpdateRequest request
    ) {
        return courseService.updateCourse(currentUser.id(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long id
    ) {
        courseService.deleteCourse(currentUser.id(), id);
    }
}

