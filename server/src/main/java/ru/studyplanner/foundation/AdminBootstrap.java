package ru.studyplanner.foundation;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.studyplanner.entity.StudentProfile;
import ru.studyplanner.entity.User;
import ru.studyplanner.entity.UserRole;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminBootstrap(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.email:}") String adminEmail,
            @Value("${app.admin.password:}") String adminPassword
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        String normalizedEmail = adminEmail.trim().toLowerCase(Locale.ROOT);
        if (normalizedEmail.isBlank() || adminPassword.isBlank() || userRepository.existsByEmail(normalizedEmail)) {
            return;
        }
        User admin = new User(normalizedEmail, passwordEncoder.encode(adminPassword), UserRole.ADMIN);
        admin.attachProfile(new StudentProfile("Администратор системы", "ADMIN"));
        userRepository.save(admin);
    }
}
