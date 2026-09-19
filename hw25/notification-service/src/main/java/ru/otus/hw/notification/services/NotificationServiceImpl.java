package ru.otus.hw.notification.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.notification.models.EmailMessage;
import ru.otus.hw.notification.repositories.EmailMessageRepository;
import ru.otus.hw.notification.services.dto.EmailMessageResponse;
import ru.otus.hw.notification.services.dto.SendEmailRequest;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmailMessageRepository emailMessageRepository;

    @Override
    @Transactional
    public EmailMessageResponse sendEmail(SendEmailRequest request) {
        var message = new EmailMessage(null, request.getRecipient(), request.getSubject(),
                request.getBody(), Instant.now());
        return toResponse(emailMessageRepository.save(message));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailMessageResponse> findByRecipient(String recipient) {
        return emailMessageRepository.findAllByRecipientOrderByIdDesc(recipient).stream()
                .map(NotificationServiceImpl::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailMessageResponse> findAll() {
        return emailMessageRepository.findAll().stream()
                .map(NotificationServiceImpl::toResponse)
                .toList();
    }

    private static EmailMessageResponse toResponse(EmailMessage message) {
        return new EmailMessageResponse(message.getId(), message.getRecipient(), message.getSubject(),
                message.getBody(), message.getSentAt());
    }
}
