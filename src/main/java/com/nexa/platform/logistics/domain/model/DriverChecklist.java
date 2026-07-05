package com.nexa.platform.logistics.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "logistics_driver_checklists")
public class DriverChecklist extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Shipment shipment;
    @Column(nullable = false)
    private boolean vehicleClean;
    @Column(nullable = false)
    private boolean temperatureRecorderReady;
    @Column(nullable = false)
    private boolean sealsVerified;
    @Column(length = 240)
    private String notes;
    protected DriverChecklist() { }
    public DriverChecklist(Shipment shipment, boolean vehicleClean, boolean temperatureRecorderReady, boolean sealsVerified, String notes) { this.shipment = shipment; this.vehicleClean = vehicleClean; this.temperatureRecorderReady = temperatureRecorderReady; this.sealsVerified = sealsVerified; this.notes = notes; }
    public Long getId() { return id; }
    public Shipment getShipment() { return shipment; }
    public boolean isVehicleClean() { return vehicleClean; }
    public boolean isTemperatureRecorderReady() { return temperatureRecorderReady; }
    public boolean isSealsVerified() { return sealsVerified; }
    public String getNotes() { return notes; }
}
