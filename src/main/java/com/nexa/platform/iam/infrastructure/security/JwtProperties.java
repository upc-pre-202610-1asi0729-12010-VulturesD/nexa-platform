package com.nexa.platform.iam.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nexa.jwt")
public record JwtProperties(String secret, long expirationMinutes) { }
