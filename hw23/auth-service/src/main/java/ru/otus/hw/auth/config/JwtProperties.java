package ru.otus.hw.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    // Must be at least 256 bits (32 bytes) for HS256.
    private String secret;

    private long expirationMinutes = 60;
}
