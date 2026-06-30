package com.nexa.platform.sales.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "sales_purchase_requests",
    indexes = @Index(name = "idx_sales_purchase_requests_code", columnList = "code", unique = true))
public class PurchaseRequest extends AuditableEntity {
    private static final Set<String> ALLOWED_PAYMENT_OPTIONS = Set.of("", "credit_line", "bank_transfer", "cash", "cash_on_delivery");

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    @Column(nullable = false, length = 40)
    private String clientId;
    @Column(nullable = false, unique = true, length = 40)
    private String code;
    @Column(nullable = false, length = 40)
    private String origin;
    @Column(nullable = false, length = 40)
    private String status;
    @Column(nullable = false, length = 40)
    private String priority;
    private LocalDate requestedDeliveryDate;
    @Column(nullable = false, length = 160)
    private String deliveryAddress;
    @Column(length = 80)
    private String deliveryDistrict;
    @Column(length = 80)
    private String deliveryCity;
    @Column(length = 80)
    private String deliveryProvince;
    @Column(length = 180)
    private String deliveryReference;
    @Column(nullable = false, length = 40)
    private String paymentOption;
    @Column(precision = 12, scale = 2)
    private BigDecimal shippingEstimate;
    @Column(length = 800)
    private String comments;
    @Column(length = 120)
    private String commercialOwner;
    private Long acceptedOrderId;
    @Column(length = 40)
    private String acceptedOrderNumber;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "purchase_request_id")
    private List<PurchaseRequestLine> items = new ArrayList<>();

    protected PurchaseRequest() { }

    public PurchaseRequest(Long tenantId, String clientId, String code, String origin, String status, String priority,
                           LocalDate requestedDeliveryDate, String deliveryAddress, String deliveryDistrict,
                           String deliveryCity, String deliveryProvince, String deliveryReference, String paymentOption,
                           BigDecimal shippingEstimate, String comments, String commercialOwner) {
        this.tenantId = requireTenant(tenantId);
        this.clientId = require(clientId, "Client id is required");
        this.code = require(code, "Purchase request code is required");
        this.origin = defaultText(origin, "buyer_portal");
        this.status = defaultText(status, "submitted").toLowerCase();
        this.priority = normalizePriority(priority);
        this.requestedDeliveryDate = requestedDeliveryDate;
        this.deliveryAddress = defaultText(deliveryAddress, "");
        this.deliveryDistrict = optional(deliveryDistrict);
        this.deliveryCity = optional(deliveryCity);
        this.deliveryProvince = optional(deliveryProvince);
        this.deliveryReference = optional(deliveryReference);
        this.paymentOption = normalizePaymentOption(paymentOption);
        this.shippingEstimate = shippingEstimate;
        this.comments = defaultText(comments, "");
        this.commercialOwner = defaultText(commercialOwner, "");
        validateStructuredFields();
    }

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public String getClientId() { return clientId; }
    public String getCode() { return code; }
    public String getOrigin() { return origin; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    public LocalDate getRequestedDeliveryDate() { return requestedDeliveryDate; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getDeliveryDistrict() { return deliveryDistrict; }
    public String getDeliveryCity() { return deliveryCity; }
    public String getDeliveryProvince() { return deliveryProvince; }
    public String getDeliveryReference() { return deliveryReference; }
    public String getPaymentOption() { return paymentOption; }
    public BigDecimal getShippingEstimate() { return shippingEstimate; }
    public String getComments() { return comments; }
    public String getCommercialOwner() { return commercialOwner; }
    public Long getAcceptedOrderId() { return acceptedOrderId; }
    public String getAcceptedOrderNumber() { return acceptedOrderNumber; }
    public List<PurchaseRequestLine> getItems() { return items; }

    public void addItem(PurchaseRequestLine item) {
        items.add(item);
    }

    public void update(String clientId, String code, String origin, String status, String priority,
                       LocalDate requestedDeliveryDate, String deliveryAddress, String deliveryDistrict,
                       String deliveryCity, String deliveryProvince, String deliveryReference, String paymentOption,
                       BigDecimal shippingEstimate, String comments, String commercialOwner) {
        this.clientId = require(clientId, "Client id is required");
        this.code = require(code, "Purchase request code is required");
        this.origin = defaultText(origin, "buyer_portal");
        this.status = defaultText(status, this.status).toLowerCase();
        this.priority = normalizePriority(priority);
        this.requestedDeliveryDate = requestedDeliveryDate;
        this.deliveryAddress = defaultText(deliveryAddress, "");
        this.deliveryDistrict = optional(deliveryDistrict);
        this.deliveryCity = optional(deliveryCity);
        this.deliveryProvince = optional(deliveryProvince);
        this.deliveryReference = optional(deliveryReference);
        this.paymentOption = normalizePaymentOption(paymentOption);
        this.shippingEstimate = shippingEstimate;
        this.comments = defaultText(comments, "");
        this.commercialOwner = defaultText(commercialOwner, "");
        validateStructuredFields();
    }

    public void replaceItems(List<PurchaseRequestLine> nextItems) {
        items.clear();
        if (nextItems != null) items.addAll(nextItems);
    }

    public void changeStatus(String status, String note, String commercialOwner) {
        String nextStatus = require(status, "Purchase request status is required").toLowerCase();
        validateTransition(nextStatus);
        this.status = nextStatus;
        if (hasText(note)) this.comments = note.trim();
        if (hasText(commercialOwner)) this.commercialOwner = commercialOwner.trim();
    }

    public void markAcceptedIntoOrder(Long orderId, String orderNumber, String note) {
        if ("converted_to_order".equals(status)) return;
        if (!"commercially_validated".equals(status)) {
            throw new IllegalStateException("Only commercially validated purchase requests can be accepted.");
        }
        if (orderId == null) throw new IllegalArgumentException("Accepted order id is required.");
        this.acceptedOrderId = orderId;
        this.acceptedOrderNumber = require(orderNumber, "Accepted order number is required.");
        this.status = "converted_to_order";
        if (hasText(note)) this.comments = note.trim();
    }

    private void validateStructuredFields() {
        if (shippingEstimate != null && shippingEstimate.signum() < 0) throw new IllegalArgumentException("Shipping estimate cannot be negative.");
    }

    private void validateTransition(String nextStatus) {
        if ("converted_to_order".equals(status)) throw new IllegalStateException("Accepted purchase requests already have an order and cannot change status.");
        if ("cancelled".equals(nextStatus)) return;
        boolean allowed = switch (status + "->" + nextStatus) {
            case "submitted->submitted",
                "submitted->buyer_adjustment_requested",
                "submitted->commercially_validated",
                "submitted->rejected",
                "buyer_adjustment_requested->submitted",
                "buyer_adjustment_requested->commercially_validated",
                "buyer_adjustment_requested->rejected",
                "commercially_validated->rejected" -> true;
            default -> false;
        };
        if (!allowed) throw new IllegalStateException("Invalid purchase request transition from " + status + " to " + nextStatus + ".");
    }

    private static String normalizePriority(String value) {
        String priority = defaultText(value, "normal").toLowerCase();
        if (!List.of("low", "normal", "high", "urgent").contains(priority)) throw new IllegalArgumentException("Unsupported purchase request priority '" + priority + "'.");
        return priority;
    }

    private static String normalizePaymentOption(String value) {
        String option = defaultText(value, "").toLowerCase();
        if (!ALLOWED_PAYMENT_OPTIONS.contains(option)) throw new IllegalArgumentException("Unsupported payment option '" + option + "'.");
        return option;
    }

    private static String require(String value, String message) {
        if (!hasText(value)) throw new IllegalArgumentException(message);
        return value.trim();
    }

    private static Long requireTenant(Long tenantId) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Tenant id is required");
        return tenantId;
    }

    private static String defaultText(String value, String fallback) {
        return hasText(value) ? value.trim() : fallback;
    }

    private static String optional(String value) {
        return hasText(value) ? value.trim() : "";
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
