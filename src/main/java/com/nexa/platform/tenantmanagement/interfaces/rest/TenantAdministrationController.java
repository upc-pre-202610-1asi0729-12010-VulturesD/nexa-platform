package com.nexa.platform.tenantmanagement.interfaces.rest;

import com.nexa.platform.tenantmanagement.application.commandservices.TenantManagementCommandService;
import com.nexa.platform.tenantmanagement.application.queryservices.TenantManagementQueryService;
import com.nexa.platform.tenantmanagement.interfaces.rest.resources.TenantManagementResources.*;
import com.nexa.platform.tenantmanagement.interfaces.rest.transform.TenantManagementResourceAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Tenant Administration", description = "Tenant members, rules, workspace and preference endpoints")
@PreAuthorize("isAuthenticated()")
public class TenantAdministrationController {
    private final TenantManagementCommandService commandService;
    private final TenantManagementQueryService queryService;
    private final CurrentWorkspaceContext workspace;

    public TenantAdministrationController(TenantManagementCommandService commandService, TenantManagementQueryService queryService,
                                          CurrentWorkspaceContext workspace) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.workspace = workspace;
    }

    @Operation(summary = "List tenant members")
    @GetMapping("/tenant-members")
    public List<TenantMemberResource> tenantMembers(@RequestParam Long tenantId) {
        return queryService.listTenantMembers(workspace.requireTenant(tenantId)).stream().map(TenantManagementResourceAssembler::toResource).toList();
    }

    @GetMapping("/tenant-members/{id}")
    public TenantMemberResource tenantMember(@PathVariable Long id) {
        var resource = TenantManagementResourceAssembler.toResource(queryService.getTenantMember(id));
        return scoped(resource, resource.tenantId());
    }

    @PostMapping("/tenant-members")
    @PreAuthorize("hasRole('ADMIN')")
    public TenantMemberResource createTenantMember(@Valid @RequestBody UpsertTenantMemberResource resource) {
        workspace.requireTenant(resource.tenantId());
        return TenantManagementResourceAssembler.toResource(commandService.handle(TenantManagementResourceAssembler.toCommand(null, resource)));
    }

    @PutMapping("/tenant-members/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TenantMemberResource updateTenantMember(@PathVariable Long id, @Valid @RequestBody UpsertTenantMemberResource resource) {
        workspace.requireTenant(resource.tenantId());
        var current = TenantManagementResourceAssembler.toResource(queryService.getTenantMember(id));
        scoped(current, current.tenantId());
        return TenantManagementResourceAssembler.toResource(commandService.handle(TenantManagementResourceAssembler.toCommand(id, resource)));
    }

    @DeleteMapping("/tenant-members/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTenantMember(@PathVariable Long id) {
        var resource = TenantManagementResourceAssembler.toResource(queryService.getTenantMember(id));
        scoped(resource, resource.tenantId());
        commandService.deleteTenantMember(id);
    }

    @GetMapping("/tenant-rules")
    public List<TenantRuleResource> tenantRules(@RequestParam Long tenantId) {
        return queryService.listTenantRules(workspace.requireTenant(tenantId)).stream().map(TenantManagementResourceAssembler::toResource).toList();
    }

    @GetMapping("/tenant-rules/{id}")
    public TenantRuleResource tenantRule(@PathVariable Long id) {
        var resource = TenantManagementResourceAssembler.toResource(queryService.getTenantRule(id));
        return scoped(resource, resource.tenantId());
    }

    @PostMapping("/tenant-rules")
    @PreAuthorize("hasRole('ADMIN')")
    public TenantRuleResource createTenantRule(@Valid @RequestBody UpsertTenantRuleResource resource) {
        workspace.requireTenant(resource.tenantId());
        return TenantManagementResourceAssembler.toResource(commandService.handle(TenantManagementResourceAssembler.toCommand(null, resource)));
    }

    @PutMapping("/tenant-rules/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TenantRuleResource updateTenantRule(@PathVariable Long id, @Valid @RequestBody UpsertTenantRuleResource resource) {
        workspace.requireTenant(resource.tenantId());
        var current = TenantManagementResourceAssembler.toResource(queryService.getTenantRule(id));
        scoped(current, current.tenantId());
        return TenantManagementResourceAssembler.toResource(commandService.handle(TenantManagementResourceAssembler.toCommand(id, resource)));
    }

    @DeleteMapping("/tenant-rules/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTenantRule(@PathVariable Long id) {
        var resource = TenantManagementResourceAssembler.toResource(queryService.getTenantRule(id));
        scoped(resource, resource.tenantId());
        commandService.deleteTenantRule(id);
    }

    @GetMapping("/tenant-custom-fields")
    public List<TenantCustomFieldResource> tenantCustomFields(@RequestParam Long tenantId) {
        return queryService.listTenantCustomFields(workspace.requireTenant(tenantId)).stream().map(TenantManagementResourceAssembler::toResource).toList();
    }

    @GetMapping("/tenant-custom-fields/{id}")
    public TenantCustomFieldResource tenantCustomField(@PathVariable Long id) {
        var resource = TenantManagementResourceAssembler.toResource(queryService.getTenantCustomField(id));
        return scoped(resource, resource.tenantId());
    }

    @PostMapping("/tenant-custom-fields")
    @PreAuthorize("hasRole('ADMIN')")
    public TenantCustomFieldResource createTenantCustomField(@Valid @RequestBody UpsertTenantCustomFieldResource resource) {
        workspace.requireTenant(resource.tenantId());
        return TenantManagementResourceAssembler.toResource(commandService.handle(TenantManagementResourceAssembler.toCommand(null, resource)));
    }

    @PutMapping("/tenant-custom-fields/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TenantCustomFieldResource updateTenantCustomField(@PathVariable Long id, @Valid @RequestBody UpsertTenantCustomFieldResource resource) {
        workspace.requireTenant(resource.tenantId());
        var current = TenantManagementResourceAssembler.toResource(queryService.getTenantCustomField(id));
        scoped(current, current.tenantId());
        return TenantManagementResourceAssembler.toResource(commandService.handle(TenantManagementResourceAssembler.toCommand(id, resource)));
    }

    @DeleteMapping("/tenant-custom-fields/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTenantCustomField(@PathVariable Long id) {
        var resource = TenantManagementResourceAssembler.toResource(queryService.getTenantCustomField(id));
        scoped(resource, resource.tenantId());
        commandService.deleteTenantCustomField(id);
    }

    @GetMapping({"/tenant-subscriptions", "/subscriptions"})
    public List<TenantSubscriptionResource> tenantSubscriptions(@RequestParam Long tenantId) {
        return queryService.listTenantSubscriptions(workspace.requireTenant(tenantId)).stream().map(TenantManagementResourceAssembler::toResource).toList();
    }

    @GetMapping({"/tenant-subscriptions/{id}", "/subscriptions/{id}"})
    public TenantSubscriptionResource tenantSubscription(@PathVariable Long id) {
        var resource = TenantManagementResourceAssembler.toResource(queryService.getTenantSubscription(id));
        return scoped(resource, resource.tenantId());
    }

    @PostMapping({"/tenant-subscriptions", "/subscriptions"})
    @PreAuthorize("hasRole('ADMIN')")
    public TenantSubscriptionResource createTenantSubscription(@Valid @RequestBody UpsertTenantSubscriptionResource resource) {
        workspace.requireTenant(resource.tenantId());
        return TenantManagementResourceAssembler.toResource(commandService.handle(TenantManagementResourceAssembler.toCommand(null, resource)));
    }

    @PutMapping({"/tenant-subscriptions/{id}", "/subscriptions/{id}"})
    @PreAuthorize("hasRole('ADMIN')")
    public TenantSubscriptionResource updateTenantSubscription(@PathVariable Long id, @Valid @RequestBody UpsertTenantSubscriptionResource resource) {
        workspace.requireTenant(resource.tenantId());
        var current = TenantManagementResourceAssembler.toResource(queryService.getTenantSubscription(id));
        scoped(current, current.tenantId());
        return TenantManagementResourceAssembler.toResource(commandService.handle(TenantManagementResourceAssembler.toCommand(id, resource)));
    }

    @DeleteMapping({"/tenant-subscriptions/{id}", "/subscriptions/{id}"})
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTenantSubscription(@PathVariable Long id) {
        var resource = TenantManagementResourceAssembler.toResource(queryService.getTenantSubscription(id));
        scoped(resource, resource.tenantId());
        commandService.deleteTenantSubscription(id);
    }

    @GetMapping("/workspaces")
    public Object workspaces(@RequestParam(required = false) Long tenantId, @RequestParam(required = false) String slug) {
        if (slug != null && !slug.isBlank()) {
            var resource = TenantManagementResourceAssembler.toResource(queryService.getWorkspaceBySlug(slug));
            return scoped(resource, resource.tenantId());
        }
        return queryService.listWorkspaces(workspace.requireTenant(tenantId)).stream().map(TenantManagementResourceAssembler::toResource).toList();
    }

    @GetMapping("/workspaces/{id}")
    public WorkspaceResource workspace(@PathVariable Long id) {
        var resource = TenantManagementResourceAssembler.toResource(queryService.getWorkspace(id));
        return scoped(resource, resource.tenantId());
    }

    @PostMapping("/workspaces")
    @PreAuthorize("hasRole('ADMIN')")
    public WorkspaceResource createWorkspace(@Valid @RequestBody UpsertWorkspaceResource resource) {
        workspace.requireTenant(resource.tenantId());
        return TenantManagementResourceAssembler.toResource(commandService.handle(TenantManagementResourceAssembler.toCommand(null, resource)));
    }

    @PutMapping("/workspaces/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public WorkspaceResource updateWorkspace(@PathVariable Long id, @Valid @RequestBody UpsertWorkspaceResource resource) {
        workspace.requireTenant(resource.tenantId());
        var current = TenantManagementResourceAssembler.toResource(queryService.getWorkspace(id));
        scoped(current, current.tenantId());
        return TenantManagementResourceAssembler.toResource(commandService.handle(TenantManagementResourceAssembler.toCommand(id, resource)));
    }

    @DeleteMapping("/workspaces/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWorkspace(@PathVariable Long id) {
        var resource = TenantManagementResourceAssembler.toResource(queryService.getWorkspace(id));
        scoped(resource, resource.tenantId());
        commandService.deleteWorkspace(id);
    }

    @GetMapping("/workspace-features")
    public List<WorkspaceFeatureResource> workspaceFeatures(@RequestParam Long workspaceId) {
        requireWorkspaceTenant(workspaceId);
        return queryService.listWorkspaceFeatures(workspaceId).stream().map(TenantManagementResourceAssembler::toResource).toList();
    }

    @GetMapping("/workspace-features/{id}")
    public WorkspaceFeatureResource workspaceFeature(@PathVariable Long id) {
        var resource = TenantManagementResourceAssembler.toResource(queryService.getWorkspaceFeature(id));
        requireWorkspaceTenant(resource.workspaceId());
        return resource;
    }

    @PostMapping("/workspace-features")
    @PreAuthorize("hasRole('ADMIN')")
    public WorkspaceFeatureResource createWorkspaceFeature(@Valid @RequestBody UpsertWorkspaceFeatureResource resource) {
        requireWorkspaceTenant(resource.workspaceId());
        return TenantManagementResourceAssembler.toResource(commandService.handle(TenantManagementResourceAssembler.toCommand(null, resource)));
    }

    @PutMapping("/workspace-features/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public WorkspaceFeatureResource updateWorkspaceFeature(@PathVariable Long id, @Valid @RequestBody UpsertWorkspaceFeatureResource resource) {
        requireWorkspaceTenant(resource.workspaceId());
        var current = TenantManagementResourceAssembler.toResource(queryService.getWorkspaceFeature(id));
        requireWorkspaceTenant(current.workspaceId());
        return TenantManagementResourceAssembler.toResource(commandService.handle(TenantManagementResourceAssembler.toCommand(id, resource)));
    }

    @DeleteMapping("/workspace-features/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWorkspaceFeature(@PathVariable Long id) {
        var resource = TenantManagementResourceAssembler.toResource(queryService.getWorkspaceFeature(id));
        requireWorkspaceTenant(resource.workspaceId());
        commandService.deleteWorkspaceFeature(id);
    }

    @GetMapping("/user-workspace-memberships")
    public List<UserWorkspaceMembershipResource> userWorkspaceMemberships(@RequestParam(required = false) Long tenantId,
                                                                          @RequestParam(required = false) Long workspaceId) {
        Long scopedTenantId = workspace.requireTenant(tenantId);
        if (workspaceId != null) requireWorkspaceTenant(workspaceId);
        return queryService.listUserWorkspaceMemberships(scopedTenantId, workspaceId).stream().map(TenantManagementResourceAssembler::toResource).toList();
    }

    @GetMapping("/user-workspace-memberships/{id}")
    public UserWorkspaceMembershipResource userWorkspaceMembership(@PathVariable Long id) {
        var resource = TenantManagementResourceAssembler.toResource(queryService.getUserWorkspaceMembership(id));
        return scoped(resource, resource.tenantId());
    }

    @PostMapping("/user-workspace-memberships")
    @PreAuthorize("hasRole('ADMIN')")
    public UserWorkspaceMembershipResource createUserWorkspaceMembership(@Valid @RequestBody UpsertUserWorkspaceMembershipResource resource) {
        workspace.requireTenant(resource.tenantId());
        requireWorkspaceTenant(resource.workspaceId());
        return TenantManagementResourceAssembler.toResource(commandService.handle(TenantManagementResourceAssembler.toCommand(null, resource)));
    }

    @PutMapping("/user-workspace-memberships/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserWorkspaceMembershipResource updateUserWorkspaceMembership(@PathVariable Long id, @Valid @RequestBody UpsertUserWorkspaceMembershipResource resource) {
        workspace.requireTenant(resource.tenantId());
        requireWorkspaceTenant(resource.workspaceId());
        var current = TenantManagementResourceAssembler.toResource(queryService.getUserWorkspaceMembership(id));
        scoped(current, current.tenantId());
        return TenantManagementResourceAssembler.toResource(commandService.handle(TenantManagementResourceAssembler.toCommand(id, resource)));
    }

    @DeleteMapping("/user-workspace-memberships/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserWorkspaceMembership(@PathVariable Long id) {
        var resource = TenantManagementResourceAssembler.toResource(queryService.getUserWorkspaceMembership(id));
        scoped(resource, resource.tenantId());
        commandService.deleteUserWorkspaceMembership(id);
    }

    @GetMapping("/workspace-preferences")
    public List<WorkspacePreferenceResource> workspacePreferences(@RequestParam Long workspaceId) {
        requireWorkspaceTenant(workspaceId);
        return queryService.listWorkspacePreferences(workspaceId).stream().map(TenantManagementResourceAssembler::toResource).toList();
    }

    @GetMapping("/workspace-preferences/{id}")
    public WorkspacePreferenceResource workspacePreference(@PathVariable Long id) {
        var resource = TenantManagementResourceAssembler.toResource(queryService.getWorkspacePreference(id));
        return scoped(resource, resource.tenantId());
    }

    @PostMapping("/workspace-preferences")
    @PreAuthorize("hasRole('ADMIN')")
    public WorkspacePreferenceResource createWorkspacePreference(@Valid @RequestBody UpsertWorkspacePreferenceResource resource) {
        workspace.requireTenant(resource.tenantId());
        requireWorkspaceTenant(resource.workspaceId());
        return TenantManagementResourceAssembler.toResource(commandService.handle(TenantManagementResourceAssembler.toCommand(null, resource)));
    }

    @PutMapping("/workspace-preferences/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public WorkspacePreferenceResource updateWorkspacePreference(@PathVariable Long id, @Valid @RequestBody UpsertWorkspacePreferenceResource resource) {
        workspace.requireTenant(resource.tenantId());
        requireWorkspaceTenant(resource.workspaceId());
        var current = TenantManagementResourceAssembler.toResource(queryService.getWorkspacePreference(id));
        scoped(current, current.tenantId());
        return TenantManagementResourceAssembler.toResource(commandService.handle(TenantManagementResourceAssembler.toCommand(id, resource)));
    }

    @DeleteMapping("/workspace-preferences/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWorkspacePreference(@PathVariable Long id) {
        var resource = TenantManagementResourceAssembler.toResource(queryService.getWorkspacePreference(id));
        scoped(resource, resource.tenantId());
        commandService.deleteWorkspacePreference(id);
    }

    private <T> T scoped(T resource, Long tenantId) {
        workspace.requireTenant(tenantId);
        return resource;
    }

    private void requireWorkspaceTenant(Long workspaceId) {
        workspace.requireTenant(queryService.getWorkspace(workspaceId).tenantId());
    }
}
