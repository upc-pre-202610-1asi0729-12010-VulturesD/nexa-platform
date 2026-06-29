package com.nexa.platform.iam.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "iam_roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 40)
    private RoleName name;

    protected Role() { }
    public Role(RoleName name) { this.name = name; }
    public Long getId() { return id; }
    public RoleName getName() { return name; }
}
