package ru.studyplanner.control;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.studyplanner.entity.Course;
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
class AssignmentControllerIntegrationTest {

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

    private User user;
    private Course course;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
        assignmentRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();
        user = new User("student@example.com", passwordEncoder.encode("password"), UserRole.STUDENT);
        user.attachProfile(new StudentProfile("Student User", "PI-01"));
        user = userRepository.save(user);
        course = courseRepository.save(new Course(user, "Software Engineering", "Teacher", (short) 6, "#2F80ED"));
    }

    @Test
    void createAssignmentValidatesRequestBody() throws Exception {
        mockMvc.perform(post("/api/assignments")
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courseId": null,
                                  "title": "",
                                  "dueAt": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void userCanCreateAndListAssignments() throws Exception {
        String dueAt = Instant.now().plusSeconds(86_400).toString();

        mockMvc.perform(post("/api/assignments")
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courseId": %d,
                                  "title": "Prepare report",
                                  "description": "Draft explanatory note",
                                  "dueAt": "%s",
                                  "priority": "HIGH",
                                  "status": "NEW"
                                }
                                """.formatted(course.getId(), dueAt)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Prepare report"))
                .andExpect(jsonPath("$.courseTitle").value("Software Engineering"));

        mockMvc.perform(get("/api/assignments")
                        .header("Authorization", bearerToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Prepare report"));
    }

    private String bearerToken() {
        return "Bearer " + jwtProvider.generateToken(user);
    }
}
