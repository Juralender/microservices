package ru.otus.hw.auth.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.auth.exceptions.InvalidCredentialsException;
import ru.otus.hw.auth.exceptions.InvalidTokenException;
import ru.otus.hw.auth.exceptions.UserAlreadyExistsException;
import ru.otus.hw.auth.services.dto.LoginRequest;
import ru.otus.hw.auth.services.dto.RegisterRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AuthServiceImplTest {

    @Autowired
    private AuthService authService;

    @Test
    void registerThenLoginProducesValidatableToken() {
        var username = "alice";
        authService.register(new RegisterRequest(username, "s3cret!"));

        var token = authService.login(new LoginRequest(username, "s3cret!"));
        assertThat(token.getToken()).isNotBlank();

        var validated = authService.validate("Bearer " + token.getToken());
        assertThat(validated.getUsername()).isEqualTo(username);
    }

    @Test
    void registeringSameUsernameTwiceFails() {
        authService.register(new RegisterRequest("bob", "s3cret!"));
        assertThatThrownBy(() -> authService.register(new RegisterRequest("bob", "other-pw")))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void loginWithWrongPasswordFails() {
        authService.register(new RegisterRequest("carol", "s3cret!"));
        assertThatThrownBy(() -> authService.login(new LoginRequest("carol", "wrong-pw")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void validatingGarbageTokenFails() {
        assertThatThrownBy(() -> authService.validate("Bearer not-a-real-token"))
                .isInstanceOf(InvalidTokenException.class);
    }
}
