package ru.otus.hw.order.clients;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;
import ru.otus.hw.order.clients.dto.NotificationEmailRequest;
import ru.otus.hw.order.exceptions.UpstreamServiceException;

@RequiredArgsConstructor
public class NotificationClient {

    private final RestClient restClient;

    public void sendEmail(NotificationEmailRequest request) {
        try {
            restClient.post()
                    .uri("/api/notifications/email")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            throw new UpstreamServiceException("Failed to send email via notification-service", ex);
        }
    }
}
