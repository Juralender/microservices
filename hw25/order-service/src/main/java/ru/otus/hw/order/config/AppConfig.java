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
    public BillingClient billingClient(RestClient.Builder restClientBuilder) {
        return new BillingClient(restClientBuilder.baseUrl(clientProperties.getBillingBaseUrl()).build());
    }

    @Bean
    public NotificationClient notificationClient(RestClient.Builder restClientBuilder) {
        return new NotificationClient(restClientBuilder.baseUrl(clientProperties.getNotificationBaseUrl()).build());
    }
}
