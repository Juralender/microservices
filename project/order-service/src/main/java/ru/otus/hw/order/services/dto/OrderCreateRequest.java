package ru.otus.hw.order.services.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

    @NotNull(message = "userId must not be null")
    private Long userId;

    @NotNull(message = "price must not be null")
    @DecimalMin(value = "0.01", message = "price must be positive")
    private BigDecimal price;
}
