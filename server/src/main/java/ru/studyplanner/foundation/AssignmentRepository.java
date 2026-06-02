package ru.studyplanner.foundation;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.studyplanner.entity.Assignment;

public interface AssignmentRepository extends JpaRepository<Assignment, Long>, JpaSpecificationExecutor<Assignment> {

    Optional<Assignment> findByIdAndUserId(Long id, Long userId);

    List<Assignment> findAllByUserIdOrderByDueAtAsc(Long userId);

    boolean existsByCourseId(Long courseId);
}
