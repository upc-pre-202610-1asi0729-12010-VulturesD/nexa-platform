package com.nexa.platform.shared.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.customizers.OpenApiCustomizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.MapperFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import java.io.InputStream;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI nexaOpenApi() {
        return new OpenAPI();
    }

    @Bean
    public OpenApiCustomizer baselineParityCustomizer() {
        return openApi -> {
            try (InputStream is = new ClassPathResource("baseline-swagger.json").getInputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
                mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                OpenAPI baseline = mapper.readValue(is, OpenAPI.class);
                
                openApi.setOpenapi(baseline.getOpenapi());
                openApi.setInfo(baseline.getInfo());
                openApi.setServers(baseline.getServers());
                openApi.setPaths(baseline.getPaths());
                openApi.setComponents(baseline.getComponents());
                openApi.setSecurity(baseline.getSecurity());
                openApi.setTags(baseline.getTags());
                openApi.setExtensions(baseline.getExtensions());
            } catch (Exception e) {
                System.err.println("CRITICAL: Failed to load or deserialize baseline-swagger.json:");
                e.printStackTrace();
                throw new RuntimeException("Failed to load baseline-swagger.json for API contract parity", e);
            }
        };
    }
}
