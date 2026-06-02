package ru.studyplanner.foundation;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.studyplanner.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByIdAndUserId(Long id, Long userId);

    List<Course> findAllByUserIdOrderByTitleAsc(Long userId);

    boolean existsByUserIdAndTitleIgnoreCase(Long userId, String title);

    boolean existsByIdAndUserId(Long id, Long userId);
}
