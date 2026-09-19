package ru.otus.hw.billing.rest.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.billing.services.AccountService;
import ru.otus.hw.billing.services.dto.AccountResponse;
import ru.otus.hw.billing.services.dto.DepositRequest;
import ru.otus.hw.billing.services.dto.WithdrawRequest;
import ru.otus.hw.billing.services.dto.WithdrawResult;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{userId}")
    public AccountResponse findByUserId(@PathVariable Long userId) {
        return accountService.findByUserId(userId);
    }

    @PostMapping("/{userId}/deposit")
    public AccountResponse deposit(@PathVariable Long userId, @Valid @RequestBody DepositRequest request) {
        return accountService.deposit(userId, request.getAmount());
    }

    @PostMapping("/{userId}/withdraw")
    public WithdrawResult withdraw(@PathVariable Long userId, @Valid @RequestBody WithdrawRequest request) {
        return accountService.withdraw(userId, request.getAmount());
    }
}
