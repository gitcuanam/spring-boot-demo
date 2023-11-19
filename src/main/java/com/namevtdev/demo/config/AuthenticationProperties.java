package com.namevtdev.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "security.authentication.jwt")
@Data
public class AuthenticationProperties {

    private String keyStore;

    private String keyStorePassword;

    private String keyAlias;

    private final Duration accessTokenExpiresIn = Duration.ofHours(1);

    private final Duration refreshTokenExpiresIn = Duration.ofDays(30);
}
