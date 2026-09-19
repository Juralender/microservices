package ru.otus.hw.billing.services.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequest {

    @NotNull(message = "Amount must not be null")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;
}
