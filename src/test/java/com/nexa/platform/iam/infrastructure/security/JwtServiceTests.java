package com.nexa.platform.iam.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JwtServiceTests {
    @Test
    void generatedTokenKeepsSubject() {
        JwtService service = new JwtService(new JwtProperties("test-secret-with-enough-length-for-hmac-signing-key", 30));
        String token = service.generateToken("admin@nexa.local");
        assertThat(service.extractSubject(token)).isEqualTo("admin@nexa.local");
    }
}
