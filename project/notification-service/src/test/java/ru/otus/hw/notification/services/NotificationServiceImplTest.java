package ru.otus.hw.notification.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.notification.services.dto.SendEmailRequest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NotificationServiceImplTest {

    @Autowired
    private NotificationService notificationService;

    @Test
    void sentEmailIsRetrievableByRecipient() {
        notificationService.sendEmail(new SendEmailRequest("alice@example.com", "Order #1 succeeded", "Thanks!"));
        notificationService.sendEmail(new SendEmailRequest("bob@example.com", "Order #2 succeeded", "Thanks!"));

        var aliceMessages = notificationService.findByRecipient("alice@example.com");

        assertThat(aliceMessages).hasSize(1);
        assertThat(aliceMessages.get(0).getSubject()).isEqualTo("Order #1 succeeded");
    }
}
