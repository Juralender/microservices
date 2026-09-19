package ru.otus.hw.order.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.otus.hw.order.clients.BillingClient;
import ru.otus.hw.order.clients.NotificationClient;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final ClientProperties clientProperties;

    @Bean
    public BillingClient billingClient() {
        return new BillingClient(RestClient.create(clientProperties.getBillingBaseUrl()));
    }

    @Bean
    public NotificationClient notificationClient() {
        return new NotificationClient(RestClient.create(clientProperties.getNotificationBaseUrl()));
    }
}
