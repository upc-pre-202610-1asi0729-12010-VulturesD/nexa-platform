package com.nexa.platform.tenantmanagement.application.dtos;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public final class TenantManagementResponses {
    private TenantManagementResponses() { }

    public record TenantResponse(Long id, String name, String legalName, String slug, String ruc, String workspaceUrl,
                                 String emailDomain, String plan, String status, String country) { }
    public record TenantPreviewResponse(String name, String slug, String workspaceUrl, String plan, String status) { }
    public record OrganizationRegistrationResponse(Long id, String externalId, String status, String companyName,
                                                   String workspaceName, String workspaceSlug, String adminEmail,
                                                   OffsetDateTime submittedAt) { }
    public record TenantMemberResponse(Long id, Long tenantId, String email, String fullName, String role,
                                       String department, String status) { }
    public record TenantRuleResponse(Long id, Long tenantId, String code, String name, String description,
                                     String category, boolean enabled) { }
    public record TenantCustomFieldResponse(Long id, Long tenantId, String code, String label,
                                            String targetResource, String fieldType, boolean required,
                                            boolean enabled) { }
    public record TenantSubscriptionResponse(Long id, Long tenantId, String plan, int seats, int warehouses,
                                             String paymentStatus, LocalDate nextBillingDate,
                                             String billingContact) { }
    public record WorkspaceResponse(Long id, Long tenantId, String name, String slug, String url, String emailDomain,
                                    String status, boolean primaryWorkspace) { }
    public record WorkspaceFeatureResponse(Long id, Long workspaceId, String code, String name, boolean enabled) { }
    public record UserWorkspaceMembershipResponse(Long id, Long tenantId, Long workspaceId, Long userId, String email,
                                                  String fullName, String role, String department, String status,
                                                  String portalAccess, Long clientAccountId) { }
    public record WorkspacePreferenceResponse(Long id, Long tenantId, Long workspaceId, String key, String value,
                                              String valueType) { }
}
