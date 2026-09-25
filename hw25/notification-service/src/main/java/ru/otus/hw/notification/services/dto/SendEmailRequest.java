package ru.otus.hw.notification.services.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailRequest {

    @NotBlank(message = "Recipient must not be empty")
    @Email(message = "Recipient must be a valid email address")
    private String recipient;

    @NotBlank(message = "Subject must not be empty")
    private String subject;

    @NotBlank(message = "Body must not be empty")
    private String body;
}
