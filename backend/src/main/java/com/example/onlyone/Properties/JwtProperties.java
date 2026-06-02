package com.example.onlyone.Properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {

    private String secret = "your-very-long-secret-key-that-is-at-least-32-characters-long";

    private long accessExpiration = 1800000L;

    private long refreshExpiration = 604800000L;
}
