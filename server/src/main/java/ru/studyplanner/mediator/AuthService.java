package ru.studyplanner.mediator;

import ru.studyplanner.mediator.dto.AuthResponse;
import ru.studyplanner.mediator.dto.CurrentUserResponse;
import ru.studyplanner.mediator.dto.LoginRequest;
import ru.studyplanner.mediator.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    CurrentUserResponse getCurrentUser(Long userId);
}
