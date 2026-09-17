package ru.otus.hw.records.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class UserHeaderProperties {

    // Header the gateway sets after a successful forwardAuth check.
    private String userHeader = "X-Username";
}
