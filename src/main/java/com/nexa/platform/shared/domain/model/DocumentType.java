package com.nexa.platform.shared.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "shared_document_types",
    indexes = @Index(name = "idx_shared_document_types_key", columnList = "type_key", unique = true))
public class DocumentType extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "type_key", nullable = false, unique = true, length = 60)
    private String key;
    @Column(nullable = false, length = 120)
    private String label;
    @Column(nullable = false)
    private boolean active;

    protected DocumentType() { }

    public DocumentType(String key, String label) {
        this.key = require(key, "Document type key is required").toLowerCase();
        this.label = require(label, "Document type label is required");
        this.active = true;
    }

    public Long getId() { return id; }
    public String getKey() { return key; }
    public String getLabel() { return label; }
    public boolean isActive() { return active; }

    public void update(String label, boolean active) {
        this.label = require(label, "Document type label is required");
        this.active = active;
    }

    private static String require(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }
}
