package ru.studyplanner.mediator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.studyplanner.entity.User;
import ru.studyplanner.entity.UserRole;
import ru.studyplanner.foundation.JwtProvider;
import ru.studyplanner.foundation.UserRepository;
import ru.studyplanner.mediator.dto.LoginRequest;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void loginRejectsInvalidPassword() {
        User user = new User("student@example.com", "hash", UserRole.STUDENT);
        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("student@example.com", "wrong")))
                .isInstanceOf(BadCredentialsException.class);
    }
}
