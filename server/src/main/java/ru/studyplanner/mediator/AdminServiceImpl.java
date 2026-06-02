package ru.studyplanner.mediator;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.studyplanner.entity.StudentProfile;
import ru.studyplanner.entity.User;
import ru.studyplanner.foundation.UserRepository;
import ru.studyplanner.mediator.dto.AdminUserResponse;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserResponse> getUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AdminUserResponse toResponse(User user) {
        StudentProfile profile = user.getProfile();
        return new AdminUserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled(),
                profile == null ? null : profile.getFullName(),
                profile == null ? null : profile.getGroupName(),
                user.getCreatedAt()
        );
    }
}
