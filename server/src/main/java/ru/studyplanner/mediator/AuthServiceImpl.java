package ru.studyplanner.mediator;

import java.util.Locale;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.studyplanner.entity.StudentProfile;
import ru.studyplanner.entity.User;
import ru.studyplanner.entity.UserRole;
import ru.studyplanner.foundation.JwtProvider;
import ru.studyplanner.foundation.UserRepository;
import ru.studyplanner.mediator.dto.AuthResponse;
import ru.studyplanner.mediator.dto.CurrentUserResponse;
import ru.studyplanner.mediator.dto.LoginRequest;
import ru.studyplanner.mediator.dto.RegisterRequest;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("User with this email already exists");
        }
        User user = new User(email, passwordEncoder.encode(request.password()), UserRole.STUDENT);
        user.attachProfile(new StudentProfile(request.fullName(), request.groupName()));
        User savedUser = userRepository.save(user);
        return toAuthResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(normalizeEmail(request.email()))
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!user.isEnabled() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        return toAuthResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public CurrentUserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        StudentProfile profile = user.getProfile();
        return new CurrentUserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                profile.getFullName(),
                profile.getGroupName()
        );
    }

    private AuthResponse toAuthResponse(User user) {
        StudentProfile profile = user.getProfile();
        return new AuthResponse(
                jwtProvider.generateToken(user),
                user.getId(),
                user.getEmail(),
                user.getRole(),
                profile.getFullName(),
                profile.getGroupName()
        );
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
