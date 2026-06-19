package com.nexa.platform.promotions.application.internal.commandservices;

import com.nexa.platform.promotions.application.commandservices.PromotionCommandFailure;
import com.nexa.platform.promotions.application.commandservices.PromotionCommandService;
import com.nexa.platform.promotions.application.commands.CreatePromotionCommand;
import com.nexa.platform.promotions.domain.model.aggregates.Promotion;
import com.nexa.platform.promotions.domain.model.repositories.PromotionRepository;
import com.nexa.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Concrete implementation of {@link PromotionCommandService}.
 *
 * <p>Follows the application-layer implementation pattern from the course
 * catch-up-platform reference: the service enforces domain invariants
 * (no duplicate promoCode), delegates persistence to the domain repository port,
 * and returns a typed {@link Result} to avoid exception-driven control flow.
 *
 * @since 1.0
 */
@Service
public class PromotionCommandServiceImpl implements PromotionCommandService {

    private final PromotionRepository promotionRepository;

    public PromotionCommandServiceImpl(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Business invariant: a promotion with the same {@code promoCode} must not
     * already exist. When the code is taken, a {@link PromotionCommandFailure#DUPLICATE_PROMO_CODE}
     * failure is returned without throwing an exception.
     */
    @Override
    @Transactional
    public Result<Promotion, PromotionCommandFailure> handle(CreatePromotionCommand command) {
        if (promotionRepository.findByPromoCode(command.promoCode()).isPresent()) {
            return Result.failure(PromotionCommandFailure.DUPLICATE_PROMO_CODE);
        }
        Promotion promotion = new Promotion(
            command.promoCode(),
            command.name(),
            command.description(),
            command.discountLabel(),
            command.visibility(),
            command.status(),
            command.notes(),
            command.productIds()
        );
        return Result.success(promotionRepository.save(promotion));
    }
}
