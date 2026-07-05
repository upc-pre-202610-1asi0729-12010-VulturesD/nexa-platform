package com.nexa.platform.tenantmanagement.application.commands;

import java.time.LocalDate;

public final class TenantManagementCommands {
    private TenantManagementCommands() { }

    public record CreateOrganizationRegistrationCommand(String externalId, String status, String companyName,
                                                        String workspaceName, String workspaceSlug, String adminEmail,
                                                        String payloadJson) { }
    public record UpsertTenantCommand(Long id, String name, String legalName, String slug, String ruc, String workspaceUrl,
                                      String emailDomain, String plan, String status, String country) { }
    public record UpsertTenantMemberCommand(Long id, Long tenantId, String email, String fullName, String role,
                                            String department, String status) { }
    public record UpsertTenantRuleCommand(Long id, Long tenantId, String code, String name, String description,
                                          String category, Boolean enabled) { }
    public record UpsertTenantCustomFieldCommand(Long id, Long tenantId, String code, String label,
                                                 String targetResource, String fieldType, Boolean required,
                                                 Boolean enabled) { }
    public record UpsertTenantSubscriptionCommand(Long id, Long tenantId, String plan, Integer seats,
                                                  Integer warehouses, String paymentStatus,
                                                  LocalDate nextBillingDate, String billingContact) { }
    public record UpsertWorkspaceCommand(Long id, Long tenantId, String name, String slug, String url, String emailDomain,
                                         String status, Boolean primaryWorkspace) { }
    public record UpsertWorkspaceFeatureCommand(Long id, Long workspaceId, String code, String name, Boolean enabled) { }
    public record UpsertUserWorkspaceMembershipCommand(Long id, Long tenantId, Long workspaceId, Long userId, String email,
                                                       String fullName, String role, String department, String status,
                                                       String portalAccess, Long clientAccountId) { }
    public record UpsertWorkspacePreferenceCommand(Long id, Long tenantId, Long workspaceId, String key, String value,
                                                   String valueType) { }
}
