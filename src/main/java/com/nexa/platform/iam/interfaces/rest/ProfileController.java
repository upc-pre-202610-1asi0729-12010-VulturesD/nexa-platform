package com.nexa.platform.iam.interfaces.rest;

import com.nexa.platform.iam.application.dtos.ChangePasswordRequest;
import com.nexa.platform.iam.application.internal.ProfileService;
import com.nexa.platform.iam.interfaces.rest.resources.ChangePasswordResource;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "Profile", description = "Authenticated user profile operations")
public class ProfileController {
    private final ProfileService service;
    private final CurrentWorkspaceContext workspace;

    public ProfileController(ProfileService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @Operation(summary = "Change current user's password",
        description = "Validates the current password and replaces it without returning credentials or a new token")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Password changed"),
        @ApiResponse(responseCode = "400", description = "Password validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Active workspace membership required")
    })
    @PostMapping("/password-changes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@Valid @RequestBody ChangePasswordResource resource) {
        Long tenantId = workspace.requireTenant(null);
        service.changePassword(tenantId, workspace.userId(),
            new ChangePasswordRequest(resource.currentPassword(), resource.newPassword(), resource.confirmPassword()));
    }
}
