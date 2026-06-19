package com.nexa.platform.warehouse.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "warehouse_locations")
public class Warehouse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false, length = 140)
    private String address;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TemperatureBand temperatureBand;
    protected Warehouse() { }
    public Warehouse(String name, String address, TemperatureBand temperatureBand) { this.name = name; this.address = address; this.temperatureBand = temperatureBand; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public TemperatureBand getTemperatureBand() { return temperatureBand; }
}
