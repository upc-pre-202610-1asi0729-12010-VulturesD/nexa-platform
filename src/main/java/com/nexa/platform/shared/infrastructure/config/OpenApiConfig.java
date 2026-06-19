package com.nexa.platform.shared.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI nexaOpenApi() {
        return new OpenAPI().info(new Info()
            .title("Nexa Platform API")
            .version("1.0.2")
            .description("RESTful API for B2B cold-chain catalog, inventory, sales, logistics, invoicing and IAM workflows."));
    }
}
