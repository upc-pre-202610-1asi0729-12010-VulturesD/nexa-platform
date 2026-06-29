package com.nexa.platform.warehouse.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "warehouse_stock_batches",
    uniqueConstraints = @UniqueConstraint(name = "uk_warehouse_stock_batches_tenant_lot", columnNames = {"tenant_id", "lot_code"}),
    indexes = @Index(name = "idx_warehouse_stock_batches_tenant", columnList = "tenant_id"))
public class StockBatch extends AuditableEntity {
    private static final Set<String> ALLOWED_STATUSES = Set.of("active", "quarantine", "expired", "depleted", "blocked");

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    @ManyToOne(optional = false)
    private InventoryItem item;
    @Column(nullable = false, length = 60)
    private String lotCode;
    private LocalDate expirationDate;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private int reservedQuantity;
    @Column(nullable = false)
    private LocalDate entryDate;
    @Column(nullable = false, length = 80)
    private String zone;
    @Column(nullable = false, length = 40)
    private String status;
    @Column(precision = 6, scale = 2)
    private BigDecimal minimumTemperature;
    @Column(precision = 6, scale = 2)
    private BigDecimal maximumTemperature;

    protected StockBatch() { }
    public StockBatch(InventoryItem item, String lotCode, LocalDate expirationDate, int quantity) {
        this(1L, item, lotCode, quantity, 0, LocalDate.now(), expirationDate, "", "active", null, null);
    }
    public StockBatch(Long tenantId, InventoryItem item, String lotCode, int quantity, int reservedQuantity,
                      LocalDate entryDate, LocalDate expirationDate, String zone, String status,
                      BigDecimal minimumTemperature, BigDecimal maximumTemperature) {
        update(tenantId, item, lotCode, quantity, reservedQuantity, entryDate, expirationDate, zone, status,
            minimumTemperature, maximumTemperature);
    }
    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public InventoryItem getItem() { return item; }
    public String getLotCode() { return lotCode; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public int getQuantity() { return quantity; }
    public int getReservedQuantity() { return reservedQuantity; }
    public LocalDate getEntryDate() { return entryDate; }
    public String getZone() { return zone; }
    public String getStatus() { return status; }
    public BigDecimal getMinimumTemperature() { return minimumTemperature; }
    public BigDecimal getMaximumTemperature() { return maximumTemperature; }

    public void update(Long tenantId, InventoryItem item, String lotCode, int quantity, int reservedQuantity,
                       LocalDate entryDate, LocalDate expirationDate, String zone, String status,
                       BigDecimal minimumTemperature, BigDecimal maximumTemperature) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Current tenant is required.");
        if (item == null) throw new IllegalArgumentException("Inventory item is required.");
        if (lotCode == null || lotCode.isBlank()) throw new IllegalArgumentException("Lot code is required.");
        if (quantity < 0 || reservedQuantity < 0 || reservedQuantity > quantity) {
            throw new IllegalArgumentException("Lot quantities are invalid.");
        }
        LocalDate effectiveEntryDate = entryDate == null ? LocalDate.now() : entryDate;
        if (expirationDate != null && expirationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expired lots cannot be created or activated.");
        }
        String normalizedStatus = status == null || status.isBlank() ? "active" : status.trim().toLowerCase();
        if (!ALLOWED_STATUSES.contains(normalizedStatus)) throw new IllegalArgumentException("Lot status is not supported.");
        if (minimumTemperature != null && maximumTemperature != null
            && minimumTemperature.compareTo(maximumTemperature) > 0) {
            throw new IllegalArgumentException("Minimum temperature cannot exceed maximum temperature.");
        }
        this.tenantId = tenantId;
        this.item = item;
        this.lotCode = lotCode.trim();
        this.quantity = quantity;
        this.reservedQuantity = reservedQuantity;
        this.entryDate = effectiveEntryDate;
        this.expirationDate = expirationDate;
        this.zone = zone == null ? "" : zone.trim();
        this.status = normalizedStatus;
        this.minimumTemperature = minimumTemperature;
        this.maximumTemperature = maximumTemperature;
    }

    public void reserve(int units) {
        if (units <= 0) throw new IllegalArgumentException("Reservation units must be positive.");
        if (reservedQuantity + units > quantity) throw new IllegalStateException("Requested units exceed lot availability.");
        reservedQuantity += units;
    }

    public void release(int units) {
        if (units <= 0 || units > reservedQuantity) {
            throw new IllegalStateException("Released units exceed lot reservation.");
        }
        reservedQuantity -= units;
    }
}
