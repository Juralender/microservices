package ru.otus.hw.notification.repositories;

import ru.otus.hw.notification.models.EmailMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailMessageRepository extends JpaRepository<EmailMessage, Long> {
    List<EmailMessage> findAllByRecipientOrderByIdDesc(String recipient);
}
