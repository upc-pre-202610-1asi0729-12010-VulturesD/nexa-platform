package com.nexa.platform.tenantmanagement.application.queryservices;

import com.nexa.platform.tenantmanagement.application.dtos.TenantManagementResponses.*;
import java.util.List;

public interface TenantManagementQueryService {
    List<TenantResponse> listTenants();
    TenantResponse getTenant(Long id);
    TenantResponse getTenantBySlug(String slug);
    List<OrganizationRegistrationResponse> listOrganizationRegistrations();
    OrganizationRegistrationResponse getOrganizationRegistration(String externalId);
    List<TenantMemberResponse> listTenantMembers(Long tenantId);
    TenantMemberResponse getTenantMember(Long id);
    List<TenantRuleResponse> listTenantRules(Long tenantId);
    TenantRuleResponse getTenantRule(Long id);
    List<TenantCustomFieldResponse> listTenantCustomFields(Long tenantId);
    TenantCustomFieldResponse getTenantCustomField(Long id);
    List<TenantSubscriptionResponse> listTenantSubscriptions(Long tenantId);
    TenantSubscriptionResponse getTenantSubscription(Long id);
    List<WorkspaceResponse> listWorkspaces(Long tenantId);
    WorkspaceResponse getWorkspace(Long id);
    WorkspaceResponse getWorkspaceBySlug(String slug);
    List<WorkspaceFeatureResponse> listWorkspaceFeatures(Long workspaceId);
    WorkspaceFeatureResponse getWorkspaceFeature(Long id);
    List<UserWorkspaceMembershipResponse> listUserWorkspaceMemberships(Long tenantId, Long workspaceId);
    UserWorkspaceMembershipResponse getUserWorkspaceMembership(Long id);
    List<WorkspacePreferenceResponse> listWorkspacePreferences(Long workspaceId);
    WorkspacePreferenceResponse getWorkspacePreference(Long id);
}
