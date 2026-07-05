package com.nexa.platform.tenantmanagement.application.commandservices;

import com.nexa.platform.tenantmanagement.application.commands.TenantManagementCommands.*;
import com.nexa.platform.tenantmanagement.application.dtos.TenantManagementResponses.*;

public interface TenantManagementCommandService {
    OrganizationRegistrationResponse handle(CreateOrganizationRegistrationCommand command);
    TenantResponse handle(UpsertTenantCommand command);
    TenantMemberResponse handle(UpsertTenantMemberCommand command);
    TenantRuleResponse handle(UpsertTenantRuleCommand command);
    TenantCustomFieldResponse handle(UpsertTenantCustomFieldCommand command);
    TenantSubscriptionResponse handle(UpsertTenantSubscriptionCommand command);
    WorkspaceResponse handle(UpsertWorkspaceCommand command);
    WorkspaceFeatureResponse handle(UpsertWorkspaceFeatureCommand command);
    UserWorkspaceMembershipResponse handle(UpsertUserWorkspaceMembershipCommand command);
    WorkspacePreferenceResponse handle(UpsertWorkspacePreferenceCommand command);
    void deleteTenantMember(Long id);
    void deleteTenantRule(Long id);
    void deleteTenantCustomField(Long id);
    void deleteTenantSubscription(Long id);
    void deleteWorkspace(Long id);
    void deleteWorkspaceFeature(Long id);
    void deleteUserWorkspaceMembership(Long id);
    void deleteWorkspacePreference(Long id);
}
