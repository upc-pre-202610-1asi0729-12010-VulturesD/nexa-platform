package com.nexa.platform.warehouse.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "warehouse_inventory_movements")
public class InventoryMovement extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private InventoryItem item;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MovementType type;
    @Column(nullable = false)
    private int quantityDelta;
    @Column(length = 240)
    private String note;
    protected InventoryMovement() { }
    public InventoryMovement(InventoryItem item, MovementType type, int quantityDelta, String note) { this.item = item; this.type = type; this.quantityDelta = quantityDelta; this.note = note; }
    public Long getId() { return id; }
    public InventoryItem getItem() { return item; }
    public MovementType getType() { return type; }
    public int getQuantityDelta() { return quantityDelta; }
    public String getNote() { return note; }
}
