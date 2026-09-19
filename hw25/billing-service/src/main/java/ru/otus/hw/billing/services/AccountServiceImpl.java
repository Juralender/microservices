package ru.otus.hw.billing.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.billing.exceptions.InvalidAmountException;
import ru.otus.hw.billing.exceptions.UserNotFoundException;
import ru.otus.hw.billing.models.Account;
import ru.otus.hw.billing.repositories.AccountRepository;
import ru.otus.hw.billing.services.dto.AccountResponse;
import ru.otus.hw.billing.services.dto.WithdrawResult;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public AccountResponse findByUserId(Long userId) {
        return toResponse(getAccount(userId));
    }

    @Override
    @Transactional
    public AccountResponse deposit(Long userId, BigDecimal amount) {
        requirePositive(amount);
        var account = getAccount(userId);
        account.setBalance(account.getBalance().add(amount));
        account.setUpdatedAt(Instant.now());
        return toResponse(account);
    }

    @Override
    @Transactional
    public WithdrawResult withdraw(Long userId, BigDecimal amount) {
        requirePositive(amount);
        var account = getAccount(userId);

        if (account.getBalance().compareTo(amount) < 0) {
            return new WithdrawResult(false, account.getBalance(), "Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));
        account.setUpdatedAt(Instant.now());
        return new WithdrawResult(true, account.getBalance(), "Withdrawal successful");
    }

    private Account getAccount(Long userId) {
        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
    }

    private static void requirePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }
    }

    private static AccountResponse toResponse(Account account) {
        return new AccountResponse(account.getUserId(), account.getBalance());
    }
}
