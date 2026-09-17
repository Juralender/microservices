package ru.otus.hw.auth.services;

import ru.otus.hw.auth.services.dto.LoginRequest;
import ru.otus.hw.auth.services.dto.RegisterRequest;
import ru.otus.hw.auth.services.dto.TokenResponse;
import ru.otus.hw.auth.services.dto.UserResponse;

public interface AuthService {
    UserResponse register(RegisterRequest request);

    TokenResponse login(LoginRequest request);

    UserResponse validate(String bearerToken);
}
