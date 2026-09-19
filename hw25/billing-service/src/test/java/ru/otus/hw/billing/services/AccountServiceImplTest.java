package ru.otus.hw.billing.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.billing.services.dto.UserCreateRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AccountServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Test
    void depositIncreasesBalance() {
        var user = userService.create(new UserCreateRequest("carol", "carol@example.com"));

        var account = accountService.deposit(user.getId(), new BigDecimal("100.00"));

        assertThat(account.getBalance()).isEqualByComparingTo("100.00");
    }

    @Test
    void withdrawWithSufficientFundsSucceedsAndDeductsBalance() {
        var user = userService.create(new UserCreateRequest("dave", "dave@example.com"));
        accountService.deposit(user.getId(), new BigDecimal("100.00"));

        var result = accountService.withdraw(user.getId(), new BigDecimal("40.00"));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getBalance()).isEqualByComparingTo("60.00");
    }

    @Test
    void withdrawWithInsufficientFundsFailsWithoutChangingBalance() {
        var user = userService.create(new UserCreateRequest("erin", "erin@example.com"));
        accountService.deposit(user.getId(), new BigDecimal("10.00"));

        var result = accountService.withdraw(user.getId(), new BigDecimal("50.00"));

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getBalance()).isEqualByComparingTo("10.00");
        assertThat(accountService.findByUserId(user.getId()).getBalance()).isEqualByComparingTo("10.00");
    }
}
