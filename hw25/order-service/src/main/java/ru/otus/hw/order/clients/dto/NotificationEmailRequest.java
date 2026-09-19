package ru.otus.hw.order.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEmailRequest {
    private String recipient;
    private String subject;
    private String body;
}
