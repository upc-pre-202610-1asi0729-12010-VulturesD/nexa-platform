package com.nexa.platform.iam.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "iam_users", indexes = {
    @Index(name = "idx_iam_users_email", columnList = "email", unique = true)
})
public class UserAccount extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(nullable = false, length = 160)
    private String passwordHash;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false, length = 32, columnDefinition = "varchar(32) default ''")
    private String phone = "";

    @Column(nullable = false, length = 8, columnDefinition = "varchar(8) default 'en'")
    private String preferredLanguage = "en";

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean criticalNotificationsEnabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "iam_user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    protected UserAccount() { }

    public UserAccount(String fullName, String email, String passwordHash) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isEnabled() { return enabled; }
    public String getPhone() { return phone; }
    public String getPreferredLanguage() { return preferredLanguage; }
    public boolean isCriticalNotificationsEnabled() { return criticalNotificationsEnabled; }
    public Set<Role> getRoles() { return roles; }
    public void addRole(Role role) { this.roles.add(role); }

    public void changePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash is required.");
        }
        this.passwordHash = passwordHash;
    }

    public void updateProfile(String fullName, String email, String phone, String preferredLanguage,
                              boolean criticalNotificationsEnabled) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("A valid email is required.");
        }
        String language = preferredLanguage == null ? "" : preferredLanguage.trim().toLowerCase();
        if (!language.equals("en") && !language.equals("es")) {
            throw new IllegalArgumentException("Preferred language must be en or es.");
        }
        this.fullName = fullName.trim();
        this.email = email.trim().toLowerCase();
        this.phone = phone == null ? "" : phone.trim();
        this.preferredLanguage = language;
        this.criticalNotificationsEnabled = criticalNotificationsEnabled;
    }
}
