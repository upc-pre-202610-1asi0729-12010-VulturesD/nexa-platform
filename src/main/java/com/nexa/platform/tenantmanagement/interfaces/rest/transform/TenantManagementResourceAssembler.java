package com.nexa.platform.tenantmanagement.interfaces.rest.transform;

import com.nexa.platform.tenantmanagement.application.commands.TenantManagementCommands.*;
import com.nexa.platform.tenantmanagement.application.dtos.TenantManagementResponses.*;
import com.nexa.platform.tenantmanagement.interfaces.rest.resources.TenantManagementResources.*;

public final class TenantManagementResourceAssembler {
    private TenantManagementResourceAssembler() { }

    public static CreateOrganizationRegistrationCommand toCommand(CreateOrganizationRegistrationResource resource) {
        return new CreateOrganizationRegistrationCommand(resource.id(), resource.status(), resource.company().legalName(),
            resource.workspace().workspaceName(), resource.workspace().workspaceSlug(), resource.administrator().email(),
            null);
    }

    public static UpsertTenantCommand toCommand(Long id, UpsertTenantResource resource) {
        return new UpsertTenantCommand(id, resource.name(), resource.legalName(), resource.slug(), resource.ruc(),
            resource.workspaceUrl(), resource.emailDomain(), resource.plan(), resource.status(), resource.country());
    }

    public static UpsertTenantMemberCommand toCommand(Long id, UpsertTenantMemberResource resource) {
        return new UpsertTenantMemberCommand(id, resource.tenantId(), resource.email(), resource.fullName(),
            resource.role(), resource.department(), resource.status());
    }

    public static UpsertTenantRuleCommand toCommand(Long id, UpsertTenantRuleResource resource) {
        return new UpsertTenantRuleCommand(id, resource.tenantId(), resource.code(), resource.name(),
            resource.description(), resource.category(), resource.enabled());
    }

    public static UpsertTenantCustomFieldCommand toCommand(Long id, UpsertTenantCustomFieldResource resource) {
        return new UpsertTenantCustomFieldCommand(id, resource.tenantId(), resource.code(), resource.label(),
            resource.targetResource(), resource.fieldType(), resource.required(), resource.enabled());
    }

    public static UpsertTenantSubscriptionCommand toCommand(Long id, UpsertTenantSubscriptionResource resource) {
        return new UpsertTenantSubscriptionCommand(id, resource.tenantId(), resource.plan(), resource.seats(),
            resource.warehouses(), resource.paymentStatus(), resource.nextBillingDate(), resource.billingContact());
    }

    public static UpsertWorkspaceCommand toCommand(Long id, UpsertWorkspaceResource resource) {
        return new UpsertWorkspaceCommand(id, resource.tenantId(), resource.name(), resource.slug(), resource.url(),
            resource.emailDomain(), resource.status(), resource.primaryWorkspace());
    }

    public static UpsertWorkspaceFeatureCommand toCommand(Long id, UpsertWorkspaceFeatureResource resource) {
        return new UpsertWorkspaceFeatureCommand(id, resource.workspaceId(), resource.code(), resource.name(),
            resource.enabled());
    }

    public static UpsertUserWorkspaceMembershipCommand toCommand(Long id, UpsertUserWorkspaceMembershipResource resource) {
        return new UpsertUserWorkspaceMembershipCommand(id, resource.tenantId(), resource.workspaceId(), resource.userId(),
            resource.email(), resource.fullName(), resource.role(), resource.department(), resource.status(),
            resource.portalAccess(), resource.clientAccountId());
    }

    public static UpsertWorkspacePreferenceCommand toCommand(Long id, UpsertWorkspacePreferenceResource resource) {
        return new UpsertWorkspacePreferenceCommand(id, resource.tenantId(), resource.workspaceId(), resource.key(),
            resource.value(), resource.valueType());
    }

    public static TenantResource toResource(TenantResponse response) {
        return new TenantResource(response.id(), response.name(), response.legalName(), response.slug(), response.ruc(),
            response.workspaceUrl(), response.emailDomain(), response.plan(), response.status(), response.country());
    }

    public static TenantPreviewResource toPreviewResource(TenantResponse response) {
        return new TenantPreviewResource(response.name(), response.slug(), response.workspaceUrl(), response.plan(), response.status());
    }

    public static OrganizationRegistrationResource toResource(OrganizationRegistrationResponse response) {
        return new OrganizationRegistrationResource(response.id(), response.externalId(), response.status(), response.companyName(),
            response.workspaceName(), response.workspaceSlug(), response.adminEmail(), response.submittedAt());
    }

    public static TenantMemberResource toResource(TenantMemberResponse response) {
        return new TenantMemberResource(response.id(), response.tenantId(), response.email(), response.fullName(),
            response.role(), response.department(), response.status());
    }

    public static TenantRuleResource toResource(TenantRuleResponse response) {
        return new TenantRuleResource(response.id(), response.tenantId(), response.code(), response.name(),
            response.description(), response.category(), response.enabled());
    }

    public static TenantCustomFieldResource toResource(TenantCustomFieldResponse response) {
        return new TenantCustomFieldResource(response.id(), response.tenantId(), response.code(), response.label(),
            response.targetResource(), response.fieldType(), response.required(), response.enabled());
    }

    public static TenantSubscriptionResource toResource(TenantSubscriptionResponse response) {
        return new TenantSubscriptionResource(response.id(), response.tenantId(), response.plan(), response.seats(),
            response.warehouses(), response.paymentStatus(), response.nextBillingDate(), response.billingContact());
    }

    public static WorkspaceResource toResource(WorkspaceResponse response) {
        return new WorkspaceResource(response.id(), response.tenantId(), response.name(), response.slug(), response.url(),
            response.emailDomain(), response.status(), response.primaryWorkspace());
    }

    public static WorkspaceFeatureResource toResource(WorkspaceFeatureResponse response) {
        return new WorkspaceFeatureResource(response.id(), response.workspaceId(), response.code(), response.name(), response.enabled());
    }

    public static UserWorkspaceMembershipResource toResource(UserWorkspaceMembershipResponse response) {
        return new UserWorkspaceMembershipResource(response.id(), response.tenantId(), response.workspaceId(), response.userId(),
            response.email(), response.fullName(), response.role(), response.department(), response.status(),
            response.portalAccess(), response.clientAccountId());
    }

    public static WorkspacePreferenceResource toResource(WorkspacePreferenceResponse response) {
        return new WorkspacePreferenceResource(response.id(), response.tenantId(), response.workspaceId(), response.key(),
            response.value(), response.valueType());
    }
}
