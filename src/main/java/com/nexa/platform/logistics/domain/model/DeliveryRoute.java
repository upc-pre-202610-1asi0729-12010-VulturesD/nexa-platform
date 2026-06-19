package com.nexa.platform.logistics.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "logistics_delivery_routes")
public class DeliveryRoute {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String name;
    @Column(nullable = false, length = 120)
    private String origin;
    @Column(nullable = false, length = 120)
    private String destination;
    protected DeliveryRoute() { }
    public DeliveryRoute(String name, String origin, String destination) { this.name = name; this.origin = origin; this.destination = destination; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
}
