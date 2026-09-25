package ru.otus.hw.billing.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawResult {
    private boolean success;
    private BigDecimal balance;
    private String message;
}
