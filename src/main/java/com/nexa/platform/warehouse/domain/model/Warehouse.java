package com.nexa.platform.warehouse.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "warehouse_locations",
    uniqueConstraints = @UniqueConstraint(name = "uk_warehouse_location_tenant_name", columnNames = {"tenant_id", "name"}),
    indexes = @Index(name = "idx_warehouse_location_tenant", columnList = "tenant_id"))
public class Warehouse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false, length = 140)
    private String address;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TemperatureBand temperatureBand;
    protected Warehouse() { }
    public Warehouse(String name, String address, TemperatureBand temperatureBand) { this(1L, name, address, temperatureBand); }
    public Warehouse(Long tenantId, String name, String address, TemperatureBand temperatureBand) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Current tenant is required.");
        this.tenantId = tenantId;
        this.name = name;
        this.address = address;
        this.temperatureBand = temperatureBand;
    }
    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public TemperatureBand getTemperatureBand() { return temperatureBand; }
}
