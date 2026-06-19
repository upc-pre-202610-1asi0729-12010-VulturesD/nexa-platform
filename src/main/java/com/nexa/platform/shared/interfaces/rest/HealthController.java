package com.nexa.platform.shared.interfaces.rest;

import com.nexa.platform.shared.interfaces.rest.resources.HealthResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {
    @GetMapping
    public HealthResource health() {
        return new HealthResource("Nexa Platform API is running");
    }
}
