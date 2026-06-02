package ru.studyplanner.control;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.studyplanner.entity.StudentProfile;
import ru.studyplanner.entity.User;
import ru.studyplanner.entity.UserRole;
import ru.studyplanner.foundation.AssignmentRepository;
import ru.studyplanner.foundation.CourseRepository;
import ru.studyplanner.foundation.JwtProvider;
import ru.studyplanner.foundation.ReminderRepository;
import ru.studyplanner.foundation.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @BeforeEach
    void cleanDatabase() {
        reminderRepository.deleteAll();
        assignmentRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void protectedEndpointRequiresJwtToken() throws Exception {
        mockMvc.perform(get("/api/assignments"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpointRequiresAdminRole() throws Exception {
        User student = saveUser("student@example.com", UserRole.STUDENT);
        User admin = saveUser("admin@example.com", UserRole.ADMIN);

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + jwtProvider.generateToken(student)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + jwtProvider.generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void invalidLoginReturnsUnauthorized() throws Exception {
        saveUser("student@example.com", UserRole.STUDENT);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "student@example.com",
                                  "password": "wrong-password"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    private User saveUser(String email, UserRole role) {
        User user = new User(email, passwordEncoder.encode("password"), role);
        user.attachProfile(new StudentProfile("Test User", "PI-01"));
        return userRepository.save(user);
    }
}
