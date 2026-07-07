package com.nexa.platform.tenantmanagement.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public final class TenantManagementResources {
    private TenantManagementResources() { }

    public record CreateOrganizationRegistrationResource(String id, String status,
        @NotNull OrganizationRegistrationCompanyResource company,
        @NotNull OrganizationRegistrationWorkspaceResource workspace,
        @NotNull OrganizationRegistrationAdministratorResource administrator) { }
    public record OrganizationRegistrationCompanyResource(@NotBlank String legalName) { }
    public record OrganizationRegistrationWorkspaceResource(@NotBlank String workspaceName, @NotBlank String workspaceSlug) { }
    public record OrganizationRegistrationAdministratorResource(@NotBlank @Email String email) { }
    public record OrganizationRegistrationResource(Long id, String externalId, String status, String companyName,
                                                   String workspaceName, String workspaceSlug, String adminEmail,
                                                   OffsetDateTime submittedAt) { }

    public record TenantResource(Long id, String name, String legalName, String slug, String ruc, String workspaceUrl,
                                 String emailDomain, String plan, String status, String country) { }
    public record TenantPreviewResource(String name, String slug, String workspaceUrl, String plan, String status) { }
    public record UpsertTenantResource(@NotBlank String name, @NotBlank String legalName, @NotBlank String slug,
                                       @NotBlank String ruc, @NotBlank String workspaceUrl, String emailDomain,
                                       String plan, String status, String country) { }

    public record TenantMemberResource(Long id, Long tenantId, String email, String fullName, String role,
                                       String department, String status) { }
    public record UpsertTenantMemberResource(@NotNull Long tenantId, @NotBlank @Email String email,
                                             @NotBlank String fullName, @NotBlank String role,
                                             String department, String status) { }

    public record TenantRuleResource(Long id, Long tenantId, String code, String name, String description,
                                     String category, boolean enabled) { }
    public record UpsertTenantRuleResource(@NotNull Long tenantId, @NotBlank String code, @NotBlank String name,
                                           String description, String category, Boolean enabled) { }

    public record TenantCustomFieldResource(Long id, Long tenantId, String code, String label, String targetResource,
                                            String fieldType, boolean required, boolean enabled) { }
    public record UpsertTenantCustomFieldResource(@NotNull Long tenantId, @NotBlank String code,
                                                  @NotBlank String label, String targetResource,
                                                  String fieldType, Boolean required, Boolean enabled) { }

    public record TenantSubscriptionResource(Long id, Long tenantId, String plan, int seats, int warehouses,
                                             String paymentStatus, LocalDate nextBillingDate,
                                             String billingContact) { }
    public record UpsertTenantSubscriptionResource(@NotNull Long tenantId, String plan, Integer seats,
                                                   Integer warehouses, String paymentStatus,
                                                   LocalDate nextBillingDate, String billingContact) { }

    public record WorkspaceResource(Long id, Long tenantId, String name, String slug, String url, String emailDomain,
                                    String status, boolean primaryWorkspace) { }
    public record UpsertWorkspaceResource(@NotNull Long tenantId, @NotBlank String name, @NotBlank String slug,
                                         @NotBlank String url, String emailDomain, String status,
                                         Boolean primaryWorkspace) { }

    public record WorkspaceFeatureResource(Long id, Long workspaceId, String code, String name, boolean enabled) { }
    public record UpsertWorkspaceFeatureResource(@NotNull Long workspaceId, @NotBlank String code, @NotBlank String name,
                                                Boolean enabled) { }

    public record UserWorkspaceMembershipResource(Long id, Long tenantId, Long workspaceId, Long userId, String email,
                                                  String fullName, String role, String department, String status,
                                                  String portalAccess, Long clientAccountId) { }
    public record UpsertUserWorkspaceMembershipResource(@NotNull Long tenantId, @NotNull Long workspaceId,
                                                       @NotNull Long userId, @NotBlank @Email String email,
                                                       @NotBlank String fullName, @NotBlank String role,
                                                       String department, String status, String portalAccess,
                                                       Long clientAccountId) { }

    public record WorkspacePreferenceResource(Long id, Long tenantId, Long workspaceId, String key, String value,
                                              String valueType) { }
    public record UpsertWorkspacePreferenceResource(@NotNull Long tenantId, @NotNull Long workspaceId,
                                                   @NotBlank String key, @NotBlank String value,
                                                   String valueType) { }
}
