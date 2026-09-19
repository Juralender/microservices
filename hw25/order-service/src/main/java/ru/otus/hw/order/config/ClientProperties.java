package ru.otus.hw.order.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.clients")
public class ClientProperties {

    // In-cluster service URLs (see BILLING_SERVICE_URL / NOTIFICATION_SERVICE_URL).
    private String billingBaseUrl = "http://billing-service:8080";
    private String notificationBaseUrl = "http://notification-service:8080";
}
