package com.nexa.platform.promotions.application.commandservices;

import com.nexa.platform.promotions.application.commands.CreatePromotionCommand;
import com.nexa.platform.promotions.domain.model.aggregates.Promotion;
import com.nexa.platform.shared.application.result.Result;

/**
 * Application service contract for promotion command (write) operations.
 *
 * <p>Follows the CQRS pattern from the course reference implementation
 * (catch-up-platform). The command method returns a {@link Result} that
 * cleanly distinguishes between a successful promotion creation and a
 * known failure such as a duplicate promo code, avoiding exception-driven
 * control flow across layer boundaries.
 *
 * @since 1.0
 */
public interface PromotionCommandService {

    /**
     * Handles the creation of a new promotion campaign.
     *
     * @param command creation command containing all campaign fields
     * @return {@link Result#success(Object)} with the created {@link Promotion} aggregate,
     *         or {@link Result#failure(Object)} with a {@link PromotionCommandFailure} reason
     * @throws IllegalArgumentException if {@code command} is null or contains blank required fields
     * @see CreatePromotionCommand
     */
    Result<Promotion, PromotionCommandFailure> handle(CreatePromotionCommand command);
}
