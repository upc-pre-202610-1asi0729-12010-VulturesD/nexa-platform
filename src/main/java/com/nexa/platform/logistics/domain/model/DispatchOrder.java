package com.nexa.platform.logistics.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Table(name = "logistics_dispatch_orders",
    indexes = @Index(name = "idx_logistics_dispatch_orders_code", columnList = "code", unique = true))
public class DispatchOrder extends AuditableEntity {
    private static final Set<String> ALLOWED_STATUSES = Set.of("ready_for_operations", "preparing", "assigned",
        "scheduled", "ready_for_route", "in_route", "delivered", "incident", "reprogrammed", "cancelled");

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    @Column(nullable = false)
    private Long orderId;
    @Column(nullable = false)
    private Long clientAccountId;
    @Column(nullable = false, unique = true, length = 60)
    private String code;
    @Column(nullable = false, length = 40)
    private String status;
    @Column(nullable = false, length = 120)
    private String routeName;
    @Column(length = 120)
    private String responsible;
    private OffsetDateTime eta;
    @Column(length = 120)
    private String deliveryWindow;

    protected DispatchOrder() { }

    public DispatchOrder(Long tenantId, Long orderId, Long clientAccountId, String code, String status, String routeName,
                         String responsible, OffsetDateTime eta, String deliveryWindow) {
        this.tenantId = requireId(tenantId, "Tenant id is required");
        this.orderId = requireId(orderId, "Order id is required");
        this.clientAccountId = requireId(clientAccountId, "Client account id is required");
        this.code = requireText(code, "Dispatch code is required");
        this.routeName = requireText(routeName, "Route name is required");
        this.status = normalizeStatus(status == null || status.isBlank() ? "ready_for_operations" : status);
        this.responsible = optional(responsible);
        this.eta = eta;
        this.deliveryWindow = optional(deliveryWindow);
    }

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Long getOrderId() { return orderId; }
    public Long getClientAccountId() { return clientAccountId; }
    public String getCode() { return code; }
    public String getStatus() { return status; }
    public String getRouteName() { return routeName; }
    public String getResponsible() { return responsible; }
    public OffsetDateTime getEta() { return eta; }
    public String getDeliveryWindow() { return deliveryWindow; }

    public void update(Long tenantId, Long orderId, Long clientAccountId, String code, String status, String routeName,
                       String responsible, OffsetDateTime eta, String deliveryWindow) {
        this.tenantId = tenantId == null ? this.tenantId : tenantId;
        this.orderId = orderId == null ? this.orderId : orderId;
        this.clientAccountId = clientAccountId == null ? this.clientAccountId : clientAccountId;
        this.code = code == null || code.isBlank() ? this.code : code.trim();
        this.routeName = routeName == null || routeName.isBlank() ? this.routeName : routeName.trim();
        this.responsible = responsible == null || responsible.isBlank() ? this.responsible : responsible.trim();
        this.eta = eta == null ? this.eta : eta;
        this.deliveryWindow = deliveryWindow == null || deliveryWindow.isBlank() ? this.deliveryWindow : deliveryWindow.trim();
        if (status != null && !status.isBlank()) changeStatus(status);
    }

    public void assign(String responsible) {
        ensureNotTerminal();
        if ("in_route".equals(status)) throw new IllegalStateException("Dispatch already started.");
        this.responsible = requireText(responsible, "Responsible user is required");
        setStatus("assigned");
    }

    public void schedule(OffsetDateTime eta, String deliveryWindow, String status) {
        if (!Set.of("ready_for_operations", "assigned", "scheduled", "reprogrammed").contains(this.status)) {
            throw new IllegalStateException("Dispatch can only be scheduled before route start.");
        }
        if (eta == null) throw new IllegalArgumentException("Schedule date is required");
        this.eta = eta;
        this.deliveryWindow = requireText(deliveryWindow, "Delivery window is required");
        setStatus(status == null || status.isBlank() ? "scheduled" : status);
    }

    public void startRoute() {
        if (!Set.of("assigned", "scheduled", "ready_for_route").contains(status)) {
            throw new IllegalStateException("Dispatch route can only start after assignment or schedule.");
        }
        setStatus("in_route");
    }

    public void complete() {
        if (!"in_route".equals(status)) throw new IllegalStateException("Dispatch can only be completed while in route.");
        setStatus("delivered");
    }

    public void incident() {
        ensureNotTerminal();
        setStatus("incident");
    }

    public void reschedule(OffsetDateTime eta, String deliveryWindow) {
        ensureNotTerminal();
        if (eta == null) throw new IllegalArgumentException("Reschedule date is required");
        this.eta = eta;
        this.deliveryWindow = requireText(deliveryWindow, "Delivery window is required");
        setStatus("reprogrammed");
    }

    public void changeStatus(String status) {
        switch (normalizeStatus(status)) {
            case "in_route" -> startRoute();
            case "delivered" -> complete();
            case "incident" -> incident();
            default -> setStatus(status);
        }
    }

    private void setStatus(String status) {
        this.status = normalizeStatus(status);
    }

    private void ensureNotTerminal() {
        if ("delivered".equals(status) || "cancelled".equals(status)) throw new IllegalStateException("Dispatch is already closed.");
    }

    private static String normalizeStatus(String status) {
        String normalized = requireText(status, "Dispatch status is required").toLowerCase();
        if (!ALLOWED_STATUSES.contains(normalized)) throw new IllegalArgumentException("Dispatch status is not supported.");
        return normalized;
    }

    private static Long requireId(Long id, String message) {
        if (id == null) throw new IllegalArgumentException(message);
        return id;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }

    private static String optional(String value) {
        return value == null || value.isBlank() ? "" : value.trim();
    }
}
