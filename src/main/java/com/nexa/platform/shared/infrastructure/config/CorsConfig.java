package com.nexa.platform.shared.infrastructure.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
    private static final List<String> DEFAULT_ALLOWED_ORIGINS = List.of(
        "http://localhost:4200",
        "http://127.0.0.1:4200",
        "http://localhost:5173",
        "http://127.0.0.1:5173",
        "http://localhost:5174",
        "http://127.0.0.1:5174"
    );

    private final Environment environment;

    public CorsConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(resolveAllowedOrigins());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private List<String> resolveAllowedOrigins() {
        List<String> origins = new ArrayList<>(DEFAULT_ALLOWED_ORIGINS);
        addConfiguredOrigins(origins, environment.getProperty("FRONTEND_ORIGIN"));
        addConfiguredOrigins(origins, environment.getProperty("nexa.cors.allowed-origins"));
        return origins.stream().distinct().toList();
    }

    private void addConfiguredOrigins(List<String> origins, String rawOrigins) {
        if (rawOrigins == null || rawOrigins.isBlank()) return;
        for (String origin : rawOrigins.split(",")) {
            String cleanOrigin = origin.trim();
            if (!cleanOrigin.isBlank()) origins.add(cleanOrigin);
        }
    }
}
