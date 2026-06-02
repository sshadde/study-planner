package ru.studyplanner.foundation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.studyplanner.entity.CurrentUser;
import ru.studyplanner.entity.User;
import ru.studyplanner.entity.UserRole;

class JwtProviderTest {

    private final JwtProvider jwtProvider = new JwtProvider(
            new ObjectMapper(),
            "test-secret-value-with-more-than-32-characters",
            60
    );

    @Test
    void generatedTokenCanBeParsed() {
        User user = new User("student@example.com", "hash", UserRole.STUDENT);
        org.springframework.test.util.ReflectionTestUtils.setField(user, "id", 42L);

        String token = jwtProvider.generateToken(user);
        CurrentUser currentUser = jwtProvider.parse(token);

        assertThat(currentUser.id()).isEqualTo(42L);
        assertThat(currentUser.email()).isEqualTo("student@example.com");
        assertThat(currentUser.role()).isEqualTo(UserRole.STUDENT);
    }

    @Test
    void tamperedTokenIsRejected() {
        User user = new User("student@example.com", "hash", UserRole.STUDENT);
        org.springframework.test.util.ReflectionTestUtils.setField(user, "id", 42L);
        String token = jwtProvider.generateToken(user);

        assertThatThrownBy(() -> jwtProvider.parse(token + "x"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

