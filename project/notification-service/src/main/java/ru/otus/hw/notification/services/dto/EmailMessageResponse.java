package ru.otus.hw.notification.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageResponse {
    private Long id;
    private String recipient;
    private String subject;
    private String body;
    private Instant sentAt;
}
