package com.nexa.platform.iam.interfaces.rest;

import com.nexa.platform.iam.application.internal.CurrentUserService;
import com.nexa.platform.iam.application.internal.AuthService;
import com.nexa.platform.iam.interfaces.rest.resources.CreateWorkspaceUserResource;
import com.nexa.platform.iam.interfaces.rest.resources.UserResource;
import com.nexa.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import jakarta.validation.Valid;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/** Inbound REST resource for authenticated identity and profile operations. */
@RestController
@RequestMapping(value = "/api/v1/users", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Identity and user profile endpoints")
public class UsersController {

    private final CurrentUserService service;
    private final AuthService authService;
    private final CurrentWorkspaceContext workspace;

    public UsersController(CurrentUserService service, AuthService authService, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.authService = authService;
        this.workspace = workspace;
    }

    /**
     * Returns the currently authenticated user's profile.
     *
     * @return current user response
     */
    @Operation(summary = "Get current authenticated user", description = "Returns the profile of the currently logged-in user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Current user profile"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/me")
    public UserResource me() {
        return UserResourceFromEntityAssembler.toResourceFromEntity(service.currentUser());
    }

    @Operation(summary = "Update current authenticated user", description = "Updates the current user's profile and preferences")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Updated current user profile"),
        @ApiResponse(responseCode = "400", description = "Profile validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Active workspace membership required")
    })
    @PutMapping("/me")
    public UserResource updateMe(@Valid @RequestBody UpdateCurrentUserResource resource) {
        return UserResourceFromEntityAssembler.toResourceFromEntity(service.updateCurrentUser(
            resource.fullName(), resource.email(), resource.phone(), resource.preferredLanguage(),
            resource.criticalNotificationsEnabled()));
    }

    /** Returns managed workspace profiles for authenticated operational use. */
    @Operation(
        summary = "List workspace profiles",
        description = "Returns the managed commercial, logistics and buyer workspace profiles."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of workspace users")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResource> list() {
        return service.listWorkspaceUsers(workspace.requireTenant(null)).stream()
            .map(UserResourceFromEntityAssembler::toResourceFromEntity)
            .toList();
    }

    @Operation(summary = "Get workspace user", description = "Returns one user only when it belongs to the authenticated tenant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Workspace user"),
        @ApiResponse(responseCode = "403", description = "Workspace administration role required"),
        @ApiResponse(responseCode = "404", description = "User does not belong to the authenticated tenant")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResource getById(@PathVariable Long id) {
        return UserResourceFromEntityAssembler.toResourceFromEntity(
            service.workspaceUser(workspace.requireTenant(null), id));
    }

    @Operation(summary = "Create a workspace user", description = "Creates an IAM account before workspace membership assignment")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResource create(@Valid @RequestBody CreateWorkspaceUserResource resource) {
        return UserResourceFromEntityAssembler.toResourceFromEntity(authService.createWorkspaceUser(
            resource.username(), resource.email(), resource.password(), resource.role(), resource.fullName()));
    }
}
