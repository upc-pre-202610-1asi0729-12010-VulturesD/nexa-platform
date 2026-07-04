package com.nexa.platform.logistics.domain.model;

import com.nexa.platform.sales.domain.model.SalesOrder;
import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "logistics_shipments")
public class Shipment extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private SalesOrder order;
    @ManyToOne(optional = false)
    private DeliveryRoute route;
    @Column(nullable = false, length = 100)
    private String carrier;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShipmentStatus status = ShipmentStatus.SCHEDULED;
    @Column(length = 240)
    private String trackingNote;
    protected Shipment() { }
    public Shipment(SalesOrder order, DeliveryRoute route, String carrier, String trackingNote) { this.order = order; this.route = route; this.carrier = carrier; this.trackingNote = trackingNote; }
    public Long getId() { return id; }
    public SalesOrder getOrder() { return order; }
    public DeliveryRoute getRoute() { return route; }
    public String getCarrier() { return carrier; }
    public ShipmentStatus getStatus() { return status; }
    public String getTrackingNote() { return trackingNote; }
}
