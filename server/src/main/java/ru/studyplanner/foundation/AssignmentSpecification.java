package ru.studyplanner.foundation;

import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import ru.studyplanner.entity.Assignment;
import ru.studyplanner.entity.AssignmentPriority;
import ru.studyplanner.entity.AssignmentStatus;

public final class AssignmentSpecification {

    private AssignmentSpecification() {
    }

    public static Specification<Assignment> byFilter(
            Long userId,
            AssignmentStatus status,
            AssignmentPriority priority,
            Long courseId,
            Instant dueFrom,
            Instant dueTo,
            String query
    ) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (priority != null) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), priority));
            }
            if (courseId != null) {
                predicates.add(criteriaBuilder.equal(root.get("course").get("id"), courseId));
            }
            if (dueFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dueAt"), dueFrom));
            }
            if (dueTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dueAt"), dueTo));
            }
            if (query != null && !query.isBlank()) {
                String pattern = "%" + query.trim().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern)
                ));
            }
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("dueAt")));
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
