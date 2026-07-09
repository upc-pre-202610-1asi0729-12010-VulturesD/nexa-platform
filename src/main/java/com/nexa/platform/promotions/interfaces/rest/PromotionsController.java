package com.nexa.platform.promotions.interfaces.rest;

import com.nexa.platform.promotions.application.queries.GetAllPromotionsQuery;
import com.nexa.platform.promotions.application.commandservices.PromotionCommandService;
import com.nexa.platform.promotions.application.queryservices.PromotionQueryService;
import com.nexa.platform.promotions.interfaces.rest.resources.PromotionResponse;
import com.nexa.platform.promotions.interfaces.rest.resources.UpsertPromotionResource;
import com.nexa.platform.promotions.interfaces.rest.transform.PromotionResponseFromEntityAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Inbound REST resource for promotion queries.
 *
 * <p>Delegates all queries to {@link PromotionQueryService} and
 * assembles the HTTP response via {@link PromotionResponseFromEntityAssembler}.
 * Follows the interfaces-layer pattern from the course catch-up-platform reference.
 *
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/api/v1/promotions", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Promotions", description = "Commercial promotion campaign endpoints")
@PreAuthorize("isAuthenticated()")
public class PromotionsController {

    private final PromotionQueryService queryService;
    private final PromotionCommandService commandService;
    private final CurrentWorkspaceContext workspace;

    public PromotionsController(PromotionQueryService queryService, PromotionCommandService commandService,
                                CurrentWorkspaceContext workspace) {
        this.queryService = queryService;
        this.commandService = commandService;
        this.workspace = workspace;
    }

    @Operation(
        summary = "List all promotions",
        description = "Returns all promotion campaigns stored in the promotions bounded context"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promotion list returned")
    })
    @GetMapping
    public List<PromotionResponse> list() {
        return queryService.list(workspace.requireTenant(null)).stream()
            .map(PromotionResponseFromEntityAssembler::toResourceFromEntity)
            .toList();
    }

    @Operation(summary = "Get promotion by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promotion returned"),
        @ApiResponse(responseCode = "404", description = "Promotion not found")
    })
    @GetMapping("/{id}")
    public PromotionResponse get(@PathVariable Long id) {
        return queryService.get(workspace.requireTenant(null), id)
            .map(PromotionResponseFromEntityAssembler::toResourceFromEntity)
            .orElseThrow(() -> new com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException("Promotion", id));
    }

    @Operation(summary = "Create promotion")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public PromotionResponse create(@RequestBody UpsertPromotionResource resource) {
        return PromotionResponseFromEntityAssembler.toResourceFromEntity(
            commandService.create(workspace.requireTenant(null), resource));
    }

    @Operation(summary = "Update promotion")
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public PromotionResponse update(@PathVariable Long id, @RequestBody UpsertPromotionResource resource) {
        return PromotionResponseFromEntityAssembler.toResourceFromEntity(
            commandService.update(workspace.requireTenant(null), id, resource));
    }

    @Operation(summary = "Patch promotion")
    @PatchMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public PromotionResponse patch(@PathVariable Long id, @RequestBody UpsertPromotionResource resource) {
        return update(id, resource);
    }

    @Operation(summary = "Activate promotion")
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public PromotionResponse activate(@PathVariable Long id) {
        return PromotionResponseFromEntityAssembler.toResourceFromEntity(
            commandService.changeStatus(workspace.requireTenant(null), id, "active"));
    }

    @Operation(summary = "Activate promotion")
    @PostMapping("/{id}/activations")
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public PromotionResponse activateAlias(@PathVariable Long id) {
        return activate(id);
    }

    @Operation(summary = "Pause promotion")
    @PutMapping("/{id}/pause")
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public PromotionResponse pause(@PathVariable Long id) {
        return PromotionResponseFromEntityAssembler.toResourceFromEntity(
            commandService.changeStatus(workspace.requireTenant(null), id, "paused"));
    }

    @Operation(summary = "Pause promotion")
    @PostMapping("/{id}/deactivations")
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public PromotionResponse pauseAlias(@PathVariable Long id) {
        return pause(id);
    }

    @Operation(summary = "Archive promotion")
    @PutMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public PromotionResponse archive(@PathVariable Long id) {
        return PromotionResponseFromEntityAssembler.toResourceFromEntity(
            commandService.changeStatus(workspace.requireTenant(null), id, "archived"));
    }

    @Operation(summary = "Delete promotion")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public void delete(@PathVariable Long id) {
        if (!commandService.delete(workspace.requireTenant(null), id)) {
            throw new com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException("Promotion", id);
        }
    }
}
