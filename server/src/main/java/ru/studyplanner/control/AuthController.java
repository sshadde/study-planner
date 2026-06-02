package ru.studyplanner.control;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.studyplanner.entity.CurrentUser;
import ru.studyplanner.mediator.AuthService;
import ru.studyplanner.mediator.dto.AuthResponse;
import ru.studyplanner.mediator.dto.CurrentUserResponse;
import ru.studyplanner.mediator.dto.LoginRequest;
import ru.studyplanner.mediator.dto.RegisterRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public CurrentUserResponse me(@AuthenticationPrincipal CurrentUser currentUser) {
        return authService.getCurrentUser(currentUser.id());
    }
}

