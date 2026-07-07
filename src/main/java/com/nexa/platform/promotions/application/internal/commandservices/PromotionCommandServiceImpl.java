package com.nexa.platform.promotions.application.internal.commandservices;

import com.nexa.platform.promotions.application.commandservices.PromotionCommandFailure;
import com.nexa.platform.promotions.application.commandservices.PromotionCommandService;
import com.nexa.platform.promotions.application.commands.CreatePromotionCommand;
import com.nexa.platform.promotions.domain.model.aggregates.Promotion;
import com.nexa.platform.promotions.domain.model.repositories.PromotionRepository;
import com.nexa.platform.promotions.interfaces.rest.resources.UpsertPromotionResource;
import com.nexa.platform.shared.application.result.Result;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import java.time.LocalDate;
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
            command.tenantId(),
            command.promoCode(),
            command.name(),
            "",
            command.description(),
            command.discountLabel(),
            command.visibility(),
            "",
            "",
            "",
            command.notes(),
            "all",
            null,
            null,
            command.status(),
            command.productIds()
        );
        return Result.success(promotionRepository.save(promotion));
    }

    @Override
    @Transactional
    public Promotion create(Long tenantId, UpsertPromotionResource resource) {
        String code = code(resource);
        promotionRepository.findByTenantIdAndPromoCode(tenantId, code).ifPresent(existing -> {
            throw new IllegalArgumentException("Promotion code already exists.");
        });
        return promotionRepository.save(new Promotion(tenantId, code, resource.name(), resource.campaign(),
            resource.description(), resource.discountLabel(), resource.visibility(), resource.commercialRule(),
            resource.adjustmentType(), resource.targetSegment(), resource.notes(), resource.catalogScope(),
            date(resource.startsOn(), resource.startDate()), date(resource.endsOn(), resource.endDate()),
            resource.status(), resource.productIds()));
    }

    @Override
    @Transactional
    public Promotion update(Long tenantId, Long id, UpsertPromotionResource resource) {
        Promotion promotion = promotionRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Promotion", id));
        String code = code(resource);
        promotionRepository.findByTenantIdAndPromoCode(tenantId, code)
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(existing -> {
                throw new IllegalArgumentException("Promotion code already exists.");
            });
        promotion.update(tenantId, code, resource.name(), resource.campaign(), resource.description(),
            resource.discountLabel(), resource.visibility(), resource.commercialRule(), resource.adjustmentType(),
            resource.targetSegment(), resource.notes(), resource.catalogScope(), date(resource.startsOn(), resource.startDate()),
            date(resource.endsOn(), resource.endDate()), resource.status(), resource.productIds());
        return promotionRepository.save(promotion);
    }

    @Override
    @Transactional
    public Promotion changeStatus(Long tenantId, Long id, String status) {
        Promotion promotion = promotionRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Promotion", id));
        promotion.changeStatus(status);
        return promotionRepository.save(promotion);
    }

    @Override
    @Transactional
    public boolean delete(Long tenantId, Long id) {
        return promotionRepository.findByIdAndTenantId(id, tenantId)
            .map(promotion -> {
                promotionRepository.delete(promotion);
                return true;
            })
            .orElse(false);
    }

    private static String code(UpsertPromotionResource resource) {
        if (resource == null) throw new IllegalArgumentException("Promotion resource is required.");
        return resource.code();
    }

    private static LocalDate date(LocalDate value, String raw) {
        if (value != null) return value;
        if (raw == null || raw.isBlank()) return null;
        return LocalDate.parse(raw);
    }
}
