package com.nexa.platform.tenantmanagement.application.internal;

import com.nexa.platform.tenantmanagement.application.dtos.TenantManagementResponses.*;
import com.nexa.platform.tenantmanagement.domain.model.*;
import org.springframework.stereotype.Component;

@Component
public class TenantManagementMapper {
    public TenantResponse toTenantResponse(Tenant tenant) {
        return new TenantResponse(tenant.getId(), tenant.getName(), tenant.getLegalName(), tenant.getSlug(), tenant.getRuc(),
            tenant.getWorkspaceUrl(), tenant.getEmailDomain(), tenant.getPlan(), tenant.getStatus(), tenant.getCountry());
    }

    public OrganizationRegistrationResponse toOrganizationRegistrationResponse(OrganizationRegistrationRequest request) {
        return new OrganizationRegistrationResponse(request.getId(), request.getExternalId(), request.getStatus(),
            request.getCompanyName(), request.getWorkspaceName(), request.getWorkspaceSlug(), request.getAdminEmail(),
            request.getSubmittedAt());
    }

    public TenantMemberResponse toTenantMemberResponse(TenantMember member) {
        return new TenantMemberResponse(member.getId(), member.getTenantId(), member.getEmail(), member.getFullName(),
            member.getRole(), member.getDepartment(), member.getStatus());
    }

    public TenantRuleResponse toTenantRuleResponse(TenantRule rule) {
        return new TenantRuleResponse(rule.getId(), rule.getTenantId(), rule.getCode(), rule.getName(),
            rule.getDescription(), rule.getCategory(), rule.isEnabled());
    }

    public TenantCustomFieldResponse toTenantCustomFieldResponse(TenantCustomField field) {
        return new TenantCustomFieldResponse(field.getId(), field.getTenantId(), field.getCode(), field.getLabel(),
            field.getTargetResource(), field.getFieldType(), field.isRequired(), field.isEnabled());
    }

    public TenantSubscriptionResponse toTenantSubscriptionResponse(TenantSubscription subscription) {
        return new TenantSubscriptionResponse(subscription.getId(), subscription.getTenantId(), subscription.getPlan(),
            subscription.getSeats(), subscription.getWarehouses(), subscription.getPaymentStatus(),
            subscription.getNextBillingDate(), subscription.getBillingContact());
    }

    public WorkspaceResponse toWorkspaceResponse(Workspace workspace) {
        return new WorkspaceResponse(workspace.getId(), workspace.getTenantId(), workspace.getName(), workspace.getSlug(),
            workspace.getUrl(), workspace.getEmailDomain(), workspace.getStatus(), workspace.isPrimaryWorkspace());
    }

    public WorkspaceFeatureResponse toWorkspaceFeatureResponse(WorkspaceFeature feature) {
        return new WorkspaceFeatureResponse(feature.getId(), feature.getWorkspaceId(), feature.getCode(),
            feature.getName(), feature.isEnabled());
    }

    public UserWorkspaceMembershipResponse toUserWorkspaceMembershipResponse(UserWorkspaceMembership membership) {
        return new UserWorkspaceMembershipResponse(membership.getId(), membership.getTenantId(), membership.getWorkspaceId(),
            membership.getUserId(), membership.getEmail(), membership.getFullName(), membership.getRole(),
            membership.getDepartment(), membership.getStatus(), membership.getPortalAccess(), membership.getClientAccountId());
    }

    public WorkspacePreferenceResponse toWorkspacePreferenceResponse(WorkspacePreference preference) {
        return new WorkspacePreferenceResponse(preference.getId(), preference.getTenantId(), preference.getWorkspaceId(),
            preference.getKey(), preference.getValue(), preference.getValueType());
    }
}
