package com.nexa.platform.shared.interfaces.rest;

import com.nexa.platform.iam.application.dtos.LoginRequest;
import com.nexa.platform.iam.application.dtos.RegisterRequest;
import com.nexa.platform.iam.application.internal.AuthService;
import com.nexa.platform.iam.interfaces.rest.resources.AuthResource;
import com.nexa.platform.iam.interfaces.rest.transform.AuthResourceAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class BaselineCompatibilityController {
    private final AuthService authService;

    public BaselineCompatibilityController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/v1/authentication/sign-in")
    public AuthResource post_api_v1_authentication_sign_in(@RequestBody(required = false) Map<String, Object> body, @RequestParam(required = false) Map<String, String> queryParams) {
        String email = requiredText(body, "email", "username");
        String password = requiredText(body, "password");
        String workspaceSlug = optionalText(body, "workspaceSlug");
        return AuthResourceAssembler.toResourceFromEntity(authService.login(new LoginRequest(email, password, workspaceSlug)));
    }

    @PostMapping("/api/v1/authentication/sign-up")
    public ResponseEntity<?> post_api_v1_authentication_sign_up(@RequestBody(required = false) Map<String, Object> body, @RequestParam(required = false) Map<String, String> queryParams) {
        String email = requiredText(body, "email", "username");
        String password = requiredText(body, "password");
        String fullName = optionalText(body, "fullName");
        if (fullName == null) fullName = email;
        AuthResource response = AuthResourceAssembler.toResourceFromEntity(authService.register(new RegisterRequest(fullName, email, password)));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private static String requiredText(Map<String, Object> body, String... keys) {
        String value = optionalText(body, keys);
        if (value == null) throw new IllegalArgumentException(String.join(" or ", keys) + " is required");
        return value;
    }

    private static String optionalText(Map<String, Object> body, String... keys) {
        if (body == null) return null;
        for (String key : keys) {
            Object raw = body.get(key);
            if (raw instanceof String text && !text.isBlank()) return text.trim();
        }
        return null;
    }

    @GetMapping("/health/live")
    public ResponseEntity<?> get_health_live(@RequestParam(required = false) Map<String, String> queryParams) {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    @GetMapping("/health/ready")
    public ResponseEntity<?> get_health_ready(@RequestParam(required = false) Map<String, String> queryParams) {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

}
