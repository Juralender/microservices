package ru.otus.hw.billing.services;

import ru.otus.hw.billing.services.dto.AccountResponse;
import ru.otus.hw.billing.services.dto.WithdrawResult;

import java.math.BigDecimal;

public interface AccountService {
    AccountResponse findByUserId(Long userId);

    AccountResponse deposit(Long userId, BigDecimal amount);

    WithdrawResult withdraw(Long userId, BigDecimal amount);
}
