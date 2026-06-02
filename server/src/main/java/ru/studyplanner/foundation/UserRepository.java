package ru.studyplanner.foundation;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.studyplanner.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByOrderByCreatedAtDesc();

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
