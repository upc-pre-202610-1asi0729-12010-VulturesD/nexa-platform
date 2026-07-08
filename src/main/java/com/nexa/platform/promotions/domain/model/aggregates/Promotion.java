package com.nexa.platform.promotions.domain.model.aggregates;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "catalog_promotions")
public class Promotion extends AuditableEntity {
    private static final Set<String> ALLOWED_STATUSES = Set.of("draft", "scheduled", "active", "paused", "archived");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String promoCode;

    @Column(nullable = false)
    private Long tenantId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 120)
    private String campaign;

    @Column(length = 255)
    private String description;

    @Column(length = 100)
    private String discountLabel;

    @Column(length = 50)
    private String visibility;

    @Column(length = 120)
    private String commercialRule;

    @Column(length = 80)
    private String adjustmentType;

    @Column(length = 80)
    private String targetSegment;

    @Column(length = 80)
    private String catalogScope;

    private LocalDate startsOn;

    private LocalDate endsOn;

    @Column(length = 50)
    private String status;

    @Column(length = 255)
    private String notes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "catalog_promotion_products", joinColumns = @JoinColumn(name = "promotion_id"))
    @Column(name = "product_sku")
    private List<String> productIds = new ArrayList<>();

    protected Promotion() {}

    public Promotion(Long tenantId, String promoCode, String name, String campaign, String description,
                     String discountLabel, String visibility, String commercialRule, String adjustmentType,
                     String targetSegment, String notes, String catalogScope, LocalDate startsOn,
                     LocalDate endsOn, String status, List<String> productIds) {
        update(tenantId, promoCode, name, campaign, description, discountLabel, visibility, commercialRule,
            adjustmentType, targetSegment, notes, catalogScope, startsOn, endsOn, status, productIds);
    }

    public Long getId() { return id; }
    public String getPromoCode() { return promoCode; }
    public Long getTenantId() { return tenantId; }
    public String getName() { return name; }
    public String getCampaign() { return campaign; }
    public String getDescription() { return description; }
    public String getDiscountLabel() { return discountLabel; }
    public String getVisibility() { return visibility; }
    public String getCommercialRule() { return commercialRule; }
    public String getAdjustmentType() { return adjustmentType; }
    public String getTargetSegment() { return targetSegment; }
    public String getCatalogScope() { return catalogScope; }
    public LocalDate getStartsOn() { return startsOn; }
    public LocalDate getEndsOn() { return endsOn; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }
    public List<String> getProductIds() { return productIds; }

    public void update(Long tenantId, String promoCode, String name, String campaign, String description,
                       String discountLabel, String visibility, String commercialRule, String adjustmentType,
                       String targetSegment, String notes, String catalogScope, LocalDate startsOn,
                       LocalDate endsOn, String status, List<String> productIds) {
        if (startsOn != null && endsOn != null && endsOn.isBefore(startsOn)) {
            throw new IllegalArgumentException("Promotion end date cannot be before start date.");
        }
        this.tenantId = requireTenant(tenantId);
        this.promoCode = require(promoCode, "Promotion code is required.");
        this.name = require(name, "Promotion name is required.");
        this.campaign = optional(campaign);
        this.description = optional(description);
        this.discountLabel = optional(discountLabel);
        this.visibility = optional(visibility, "buyer_portal");
        this.commercialRule = optional(commercialRule);
        this.adjustmentType = optional(adjustmentType);
        this.targetSegment = optional(targetSegment);
        this.notes = optional(notes);
        this.catalogScope = optional(catalogScope, "all");
        this.startsOn = startsOn;
        this.endsOn = endsOn;
        changeStatus(status == null || status.isBlank() ? "draft" : status);
        this.productIds.clear();
        if (productIds != null) this.productIds.addAll(productIds.stream().filter(value -> value != null && !value.isBlank()).map(String::trim).toList());
    }

    public void changeStatus(String status) {
        String normalized = require(status, "Promotion status is required.").toLowerCase();
        if (!ALLOWED_STATUSES.contains(normalized)) throw new IllegalArgumentException("Promotion status is not supported.");
        this.status = normalized;
    }

    private static String require(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }

    private static Long requireTenant(Long tenantId) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Tenant id is required.");
        return tenantId;
    }

    private static String optional(String value) {
        return optional(value, "");
    }

    private static String optional(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
