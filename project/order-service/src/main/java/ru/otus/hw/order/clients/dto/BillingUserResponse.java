package ru.otus.hw.order.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingUserResponse {
    private Long id;
    private String username;
    private String email;
}
