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
    protected Category() { }
    public Category(String name, String description) { this.name = name; this.description = description; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}
