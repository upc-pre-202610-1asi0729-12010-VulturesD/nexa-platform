package com.nexa.platform.iam.interfaces.rest;

import com.nexa.platform.iam.application.internal.CurrentUserService;
import com.nexa.platform.iam.interfaces.rest.resources.UserResource;
import com.nexa.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Inbound REST resource for identity-related user queries.
 *
 * <p>The {@code GET /api/v1/users} listing endpoint intentionally exposes only
 * the three named B2B workspace profiles (Valeria Sanchez — Commercial,
 * Roberto Garcia — Logistics, Elena Litano — Buyer portal). Internal system
 * accounts (admin, operator, warehouse) are excluded so the login quick-access
 * panel shows exactly the three intended workspace personas.
 *
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/api/v1/users", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Identity and user profile endpoints")
public class UsersController {

    private final CurrentUserService service;

    public UsersController(CurrentUserService service) {
        this.service = service;
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

    /**
     * Returns the three named B2B workspace profiles used on the login
     * quick-access panel. System / internal accounts are excluded.
     *
     * @return list of workspace user profiles
     */
    @Operation(
        summary = "List workspace profiles",
        description = "Returns the three named B2B workspace user profiles (commercial, logistics, buyer). " +
                      "Internal system accounts are excluded. Used by the frontend login quick-access panel."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of workspace users")
    })
    @GetMapping
    public List<UserResource> list() {
        return service.listWorkspaceUsers().stream()
            .map(UserResourceFromEntityAssembler::toResourceFromEntity)
            .toList();
    }
}
