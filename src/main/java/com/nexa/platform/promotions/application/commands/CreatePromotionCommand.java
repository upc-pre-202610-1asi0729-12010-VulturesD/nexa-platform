package com.nexa.platform.promotions.application.commands;

import java.util.List;

/**
 * Command for creating a new promotion campaign.
 *
 * <p>Follows the command record pattern from the course reference implementation
 * (catch-up-platform). All fields are validated at the command service boundary
 * before the aggregate is created.
 *
 * @param promoCode     unique campaign code (e.g. "PROMO-COLD-001")
 * @param name          human-readable campaign name
 * @param description   campaign description visible to buyers or commercial team
 * @param discountLabel short label describing the commercial rule (e.g. "8% adjustment")
 * @param visibility    audience scope: "buyer_portal", "client_specific", or "internal"
 * @param status        initial campaign status: "active", "scheduled", or "draft"
 * @param notes         internal operational notes
 * @param productIds    list of product SKUs linked to this campaign
 * @since 1.0
 */
public record CreatePromotionCommand(
    String promoCode,
    String name,
    String description,
    String discountLabel,
    String visibility,
    String status,
    String notes,
    List<String> productIds
) {
    /**
     * Compact constructor — validates required fields.
     *
     * @throws IllegalArgumentException if {@code promoCode} or {@code name} is null or blank
     */
    public CreatePromotionCommand {
        if (promoCode == null || promoCode.isBlank())
            throw new IllegalArgumentException("promoCode cannot be null or blank");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("name cannot be null or blank");
    }
}
