package com.nexa.platform.tenantmanagement.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenant_management_organization_registration_requests",
    indexes = @Index(name = "idx_tenant_management_registration_external_id", columnList = "external_id", unique = true))
public class OrganizationRegistrationRequest extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true, length = 80)
    private String externalId;

    @Column(nullable = false, length = 40)
    private String status = "pending_review";

    @Column(nullable = false, length = 160)
    private String companyName;

    @Column(nullable = false, length = 120)
    private String workspaceName;

    @Column(nullable = false, length = 80)
    private String workspaceSlug;

    @Column(nullable = false, length = 180)
    private String adminEmail;

    @Column(columnDefinition = "TEXT")
    private String payloadJson;

    @Column(nullable = false)
    private OffsetDateTime submittedAt;

    protected OrganizationRegistrationRequest() { }

    public OrganizationRegistrationRequest(String externalId, String status, String companyName, String workspaceName,
                                           String workspaceSlug, String adminEmail, String payloadJson) {
        this.externalId = (externalId == null || externalId.isBlank()) ? UUID.randomUUID().toString() : externalId.trim();
        this.status = status == null || status.isBlank() ? "pending_review" : status.trim();
        this.companyName = requireText(companyName, "Company name is required");
        this.workspaceName = requireText(workspaceName, "Workspace name is required");
        this.workspaceSlug = requireText(workspaceSlug, "Workspace slug is required").toLowerCase();
        this.adminEmail = requireText(adminEmail, "Admin email is required").toLowerCase();
        this.payloadJson = payloadJson;
        this.submittedAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public String getExternalId() { return externalId; }
    public String getStatus() { return status; }
    public String getCompanyName() { return companyName; }
    public String getWorkspaceName() { return workspaceName; }
    public String getWorkspaceSlug() { return workspaceSlug; }
    public String getAdminEmail() { return adminEmail; }
    public String getPayloadJson() { return payloadJson; }
    public OffsetDateTime getSubmittedAt() { return submittedAt; }

    public void updateStatus(String status) {
        this.status = requireText(status, "Registration status is required");
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }
}
