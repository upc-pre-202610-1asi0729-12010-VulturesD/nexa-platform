package com.nexa.platform.promotions.application.commandservices;

/**
 * Enumeration of possible command failure reasons for promotion operations.
 *
 * <p>Used as the failure type in {@code Result<Promotion, PromotionCommandFailure>}
 * responses from {@link PromotionCommandService}.
 *
 * @since 1.0
 */
public enum PromotionCommandFailure {

    /**
     * A promotion with the same {@code promoCode} already exists in the system.
     */
    DUPLICATE_PROMO_CODE,

    /**
     * The command contained an invalid or unrecognised status value.
     */
    INVALID_STATUS,

    /**
     * The command contained an invalid visibility scope value.
     */
    INVALID_VISIBILITY
}
