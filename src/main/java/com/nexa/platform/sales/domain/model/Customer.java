package com.nexa.platform.sales.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sales_customers",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_sales_customer_tenant_code", columnNames = {"tenant_id", "code"}),
        @UniqueConstraint(name = "uk_sales_customer_tenant_tax_id", columnNames = {"tenant_id", "tax_id"})
    })
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    @Column(nullable = false, length = 40)
    private String code;
    @Column(nullable = false, length = 140)
    private String businessName;
    @Column(nullable = false, length = 140)
    private String commercialName;
    @Column(name = "tax_id", nullable = false, length = 40)
    private String taxId;
    @Column(nullable = false, length = 60)
    private String segment = "B2B";
    @Column(nullable = false, length = 120)
    private String contact = "";
    @Column(nullable = false, length = 160)
    private String contactEmail;
    @Column(nullable = false, length = 40)
    private String phone = "";
    @Column(nullable = false, length = 120)
    private String deliveryAddress;
    @Column(nullable = false, length = 80)
    private String district = "";
    @Column(nullable = false, length = 80)
    private String province = "";
    @Column(nullable = false, length = 180)
    private String deliveryReference = "";
    @Column(nullable = false, length = 80)
    private String documentProfile = "ruc_factura_xml_pdf_guia";
    @Column(nullable = false, length = 40)
    private String paymentCondition = "credit_15";
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal monthlyCreditLimit = BigDecimal.ZERO;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal monthlyCreditUsed = BigDecimal.ZERO;
    @Column(nullable = false, length = 40)
    private String monthlyCreditStatus = "ok";
    @Column(nullable = false, length = 120)
    private String deliveryPreference = "";
    @Column(nullable = false)
    private boolean portalAccess;
    @Column(nullable = false, length = 160)
    private String sellerWorkspaceEmail = "";
    @Column(nullable = false, length = 40)
    private String status = "active";

    protected Customer() { }
    public Customer(String businessName, String taxId, String contactEmail, String deliveryAddress) {
        this(1L, null, businessName, businessName, taxId, "B2B", "", contactEmail, "", deliveryAddress,
            "", "", "", "ruc_factura_xml_pdf_guia", "credit_15", BigDecimal.ZERO, BigDecimal.ZERO,
            "ok", "", true, "", "active");
    }
    public Customer(Long tenantId, String code, String businessName, String commercialName, String taxId,
                    String segment, String contact, String contactEmail, String phone, String deliveryAddress,
                    String district, String province, String deliveryReference, String documentProfile,
                    String paymentCondition, BigDecimal monthlyCreditLimit, BigDecimal monthlyCreditUsed,
                    String monthlyCreditStatus, String deliveryPreference, boolean portalAccess,
                    String sellerWorkspaceEmail, String status) {
        this.tenantId = requireTenant(tenantId);
        this.code = normalizeCode(code, taxId);
        apply(businessName, commercialName, taxId, segment, contact, contactEmail, phone, deliveryAddress,
            district, province, deliveryReference, documentProfile, paymentCondition, monthlyCreditLimit,
            monthlyCreditUsed, monthlyCreditStatus, deliveryPreference, portalAccess, sellerWorkspaceEmail, status);
    }

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public String getCode() { return code; }
    public String getBusinessName() { return businessName; }
    public String getCommercialName() { return commercialName; }
    public String getTaxId() { return taxId; }
    public String getSegment() { return segment; }
    public String getContact() { return contact; }
    public String getContactEmail() { return contactEmail; }
    public String getPhone() { return phone; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getDistrict() { return district; }
    public String getProvince() { return province; }
    public String getDeliveryReference() { return deliveryReference; }
    public String getDocumentProfile() { return documentProfile; }
    public String getPaymentCondition() { return paymentCondition; }
    public BigDecimal getMonthlyCreditLimit() { return monthlyCreditLimit; }
    public BigDecimal getMonthlyCreditUsed() { return monthlyCreditUsed; }
    public BigDecimal getMonthlyCreditAvailable() {
        return monthlyCreditLimit.subtract(monthlyCreditUsed).max(BigDecimal.ZERO);
    }
    public String getMonthlyCreditStatus() { return monthlyCreditStatus; }
    public String getDeliveryPreference() { return deliveryPreference; }
    public boolean isPortalAccess() { return portalAccess; }
    public String getSellerWorkspaceEmail() { return sellerWorkspaceEmail; }
    public String getStatus() { return status; }

    public void update(String businessName, String commercialName, String taxId, String segment, String contact,
                       String contactEmail, String phone, String deliveryAddress, String district, String province,
                       String deliveryReference, String documentProfile, String paymentCondition,
                       BigDecimal monthlyCreditLimit, BigDecimal monthlyCreditUsed, String monthlyCreditStatus,
                       String deliveryPreference, boolean portalAccess, String sellerWorkspaceEmail, String status) {
        apply(businessName, commercialName, taxId, segment, contact, contactEmail, phone, deliveryAddress,
            district, province, deliveryReference, documentProfile, paymentCondition, monthlyCreditLimit,
            monthlyCreditUsed, monthlyCreditStatus, deliveryPreference, portalAccess, sellerWorkspaceEmail, status);
    }

    private void apply(String businessName, String commercialName, String taxId, String segment, String contact,
                       String contactEmail, String phone, String deliveryAddress, String district, String province,
                       String deliveryReference, String documentProfile, String paymentCondition,
                       BigDecimal monthlyCreditLimit, BigDecimal monthlyCreditUsed, String monthlyCreditStatus,
                       String deliveryPreference, boolean portalAccess, String sellerWorkspaceEmail, String status) {
        this.businessName = require(businessName, "Business name is required.");
        this.commercialName = optional(commercialName, this.businessName);
        this.taxId = require(taxId, "Tax id is required.");
        this.segment = optional(segment, "B2B");
        this.contact = optional(contact, "");
        this.contactEmail = optional(contactEmail, "").toLowerCase();
        this.phone = optional(phone, "");
        this.deliveryAddress = optional(deliveryAddress, "");
        this.district = optional(district, "");
        this.province = optional(province, "");
        this.deliveryReference = optional(deliveryReference, "");
        this.documentProfile = optional(documentProfile, "ruc_factura_xml_pdf_guia").toLowerCase();
        this.paymentCondition = optional(paymentCondition, "credit_15").toLowerCase();
        this.monthlyCreditLimit = nonNegative(monthlyCreditLimit, "Monthly credit limit");
        this.monthlyCreditUsed = nonNegative(monthlyCreditUsed, "Monthly credit used");
        this.monthlyCreditStatus = optional(monthlyCreditStatus, "ok").toLowerCase();
        this.deliveryPreference = optional(deliveryPreference, "");
        this.portalAccess = portalAccess;
        this.sellerWorkspaceEmail = optional(sellerWorkspaceEmail, "").toLowerCase();
        this.status = optional(status, "active").toLowerCase();
    }

    private static Long requireTenant(Long tenantId) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Tenant id must be positive.");
        return tenantId;
    }

    private static String normalizeCode(String code, String taxId) {
        String value = code == null || code.isBlank() ? "CLI-" + require(taxId, "Tax id is required.") : code;
        return value.trim().toUpperCase();
    }

    private static String require(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }

    private static String optional(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private static BigDecimal nonNegative(BigDecimal value, String field) {
        BigDecimal normalized = value == null ? BigDecimal.ZERO : value;
        if (normalized.signum() < 0) throw new IllegalArgumentException(field + " cannot be negative.");
        return normalized;
    }
}
