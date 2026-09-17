package ru.otus.hw.auth.rest.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.auth.services.AuthService;
import ru.otus.hw.auth.services.dto.LoginRequest;
import ru.otus.hw.auth.services.dto.RegisterRequest;
import ru.otus.hw.auth.services.dto.TokenResponse;
import ru.otus.hw.auth.services.dto.UserResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // Called by Traefik's forwardAuth middleware, not by end users directly.
    @GetMapping("/validate")
    public ResponseEntity<Void> validate(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        var user = authService.validate(authorization);
        return ResponseEntity.ok()
                .header("X-User-Id", String.valueOf(user.getId()))
                .header("X-Username", user.getUsername())
                .build();
    }

    // Reachable only through the gateway's forwardAuth-protected router; see dynamic.yml.
    // JWTs are stateless here (no blacklist), so this just confirms the caller was
    // authenticated; the client is responsible for discarding the token afterward.
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader("X-Username") String username) {
        // no-op: nothing to invalidate server-side
    }
}
