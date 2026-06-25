package com.nexa.platform.iam.interfaces.rest;

import com.nexa.platform.iam.application.internal.AuthService;
import com.nexa.platform.iam.interfaces.rest.resources.AuthResource;
import com.nexa.platform.iam.interfaces.rest.resources.LoginResource;
import com.nexa.platform.iam.interfaces.rest.resources.RegisterResource;
import com.nexa.platform.iam.interfaces.rest.transform.AuthResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Inbound REST resource for authentication operations.
 *
 * <p>Handles user registration and login, delegating all logic to
 * {@link AuthService} in the application layer.
 *
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/api/v1/auth", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account and returns a JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResource register(@Valid @RequestBody RegisterResource resource) {
        return AuthResourceAssembler.toResourceFromEntity(
            service.register(AuthResourceAssembler.toRequestFromResource(resource)));
    }

    @Operation(summary = "Login", description = "Authenticates a user with email and password and returns a JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public AuthResource login(@Valid @RequestBody LoginResource resource) {
        return AuthResourceAssembler.toResourceFromEntity(
            service.login(AuthResourceAssembler.toRequestFromResource(resource)));
    }
}
