package com.nexa.platform.promotions.domain.model.aggregates;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "catalog_promotions")
public class Promotion extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String promoCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(length = 100)
    private String discountLabel;

    @Column(length = 50)
    private String visibility;

    @Column(length = 50)
    private String status;

    @Column(length = 255)
    private String notes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "catalog_promotion_products", joinColumns = @JoinColumn(name = "promotion_id"))
    @Column(name = "product_sku")
    private List<String> productIds = new ArrayList<>();

    protected Promotion() {}

    public Promotion(String promoCode, String name, String description, String discountLabel, 
                     String visibility, String status, String notes, List<String> productIds) {
        this.promoCode = promoCode;
        this.name = name;
        this.description = description;
        this.discountLabel = discountLabel;
        this.visibility = visibility;
        this.status = status;
        this.notes = notes;
        if (productIds != null) {
            this.productIds.addAll(productIds);
        }
    }

    public Long getId() { return id; }
    public String getPromoCode() { return promoCode; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getDiscountLabel() { return discountLabel; }
    public String getVisibility() { return visibility; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }
    public List<String> getProductIds() { return productIds; }

    public void setStatus(String status) {
        this.status = status;
    }
}
