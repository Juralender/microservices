package ru.otus.hw.billing.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.billing.exceptions.UserAlreadyExistsException;
import ru.otus.hw.billing.services.dto.UserCreateRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Test
    void creatingUserAlsoCreatesZeroBalanceAccount() {
        var user = userService.create(new UserCreateRequest("alice", "alice@example.com"));

        var account = accountService.findByUserId(user.getId());
        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void duplicateUsernameIsRejected() {
        userService.create(new UserCreateRequest("bob", "bob@example.com"));

        assertThatThrownBy(() -> userService.create(new UserCreateRequest("bob", "other@example.com")))
                .isInstanceOf(UserAlreadyExistsException.class);
    }
}
