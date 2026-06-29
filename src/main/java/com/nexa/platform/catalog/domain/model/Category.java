package com.nexa.platform.catalog.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "catalog_categories")
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 80)
    private String name;
    @Column(length = 240)
    private String description;
    @Column(nullable = false)
    private boolean active = true;
    protected Category() { }
    public Category(String name, String description) {
        update(name, description);
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
    public void update(String name, String description) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Category name is required.");
        this.name = name.trim();
        this.description = description == null ? "" : description.trim();
    }
    public void deactivate() { this.active = false; }
}
