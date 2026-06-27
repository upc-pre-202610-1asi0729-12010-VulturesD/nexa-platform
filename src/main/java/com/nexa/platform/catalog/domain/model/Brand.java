package com.nexa.platform.catalog.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "catalog_brands", indexes = {
    @Index(name = "idx_catalog_brands_name", columnList = "name", unique = true)
})
public class Brand extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(nullable = false, length = 240)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    protected Brand() { }

    public Brand(String name, String description) {
        update(name, description);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }

    public void update(String name, String description) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Brand name is required.");
        this.name = name.trim();
        this.description = description == null ? "" : description.trim();
    }

    public void deactivate() { this.active = false; }
}
