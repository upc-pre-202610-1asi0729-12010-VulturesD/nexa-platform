package com.nexa.platform.sales.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "sales_customers")
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 140)
    private String businessName;
    @Column(nullable = false, length = 40)
    private String taxId;
    @Column(nullable = false, length = 160)
    private String contactEmail;
    @Column(nullable = false, length = 120)
    private String deliveryAddress;
    protected Customer() { }
    public Customer(String businessName, String taxId, String contactEmail, String deliveryAddress) { this.businessName = businessName; this.taxId = taxId; this.contactEmail = contactEmail; this.deliveryAddress = deliveryAddress; }
    public Long getId() { return id; }
    public String getBusinessName() { return businessName; }
    public String getTaxId() { return taxId; }
    public String getContactEmail() { return contactEmail; }
    public String getDeliveryAddress() { return deliveryAddress; }
}
