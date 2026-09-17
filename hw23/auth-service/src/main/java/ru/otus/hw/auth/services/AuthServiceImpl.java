package ru.otus.hw.auth.services;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.auth.exceptions.InvalidCredentialsException;
import ru.otus.hw.auth.exceptions.InvalidTokenException;
import ru.otus.hw.auth.exceptions.UserAlreadyExistsException;
import ru.otus.hw.auth.models.User;
import ru.otus.hw.auth.repositories.UserRepository;
import ru.otus.hw.auth.services.dto.LoginRequest;
import ru.otus.hw.auth.services.dto.RegisterRequest;
import ru.otus.hw.auth.services.dto.TokenResponse;
import ru.otus.hw.auth.services.dto.UserResponse;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String BEARER_PREFIX = "Bearer ";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException(
                    "User with username '" + request.getUsername() + "' already exists");
        }
        var user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(Instant.now());
        var saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
        var token = jwtService.generateToken(user.getId(), user.getUsername());
        return new TokenResponse(token, "Bearer");
    }

    @Override
    public UserResponse validate(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new InvalidTokenException("Missing or malformed Authorization header");
        }
        var token = authorizationHeader.substring(BEARER_PREFIX.length());
        Claims claims = jwtService.parseAndValidate(token);
        var userId = claims.get("uid", Long.class);
        return new UserResponse(userId, claims.getSubject());
    }
}
