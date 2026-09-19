package ru.otus.hw.order.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingWithdrawResult {
    private boolean success;
    private BigDecimal balance;
    private String message;
}
