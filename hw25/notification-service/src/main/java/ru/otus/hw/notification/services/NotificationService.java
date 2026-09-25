package ru.otus.hw.notification.services;

import ru.otus.hw.notification.services.dto.EmailMessageResponse;
import ru.otus.hw.notification.services.dto.SendEmailRequest;

import java.util.List;

public interface NotificationService {
    EmailMessageResponse sendEmail(SendEmailRequest request);

    List<EmailMessageResponse> findByRecipient(String recipient);

    List<EmailMessageResponse> findAll();
}
