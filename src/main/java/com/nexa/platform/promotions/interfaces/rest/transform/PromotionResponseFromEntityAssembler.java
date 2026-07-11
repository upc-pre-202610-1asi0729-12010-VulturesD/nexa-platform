package com.nexa.platform.promotions.interfaces.rest.transform;

import com.nexa.platform.promotions.domain.model.aggregates.Promotion;
import com.nexa.platform.promotions.interfaces.rest.resources.PromotionResponse;
import java.util.List;

public class PromotionResponseFromEntityAssembler {
    public static PromotionResponse toResourceFromEntity(Promotion entity) {
        return new PromotionResponse(
            entity.getId(),
            entity.getTenantId(),
            entity.getPromoCode(),
            entity.getName(),
            entity.getCampaign(),
            entity.getDescription(),
            entity.getDiscountLabel(),
            entity.getVisibility(),
            entity.getCommercialRule(),
            entity.getAdjustmentType(),
            entity.getTargetSegment(),
            entity.getNotes(),
            entity.getCatalogScope(),
            entity.getStartsOn(),
            entity.getEndsOn(),
            entity.getStatus(),
            List.copyOf(entity.getProductIds()),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
