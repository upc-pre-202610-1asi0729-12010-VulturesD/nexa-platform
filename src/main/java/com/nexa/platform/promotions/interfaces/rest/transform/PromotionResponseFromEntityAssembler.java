package com.nexa.platform.promotions.interfaces.rest.transform;

import com.nexa.platform.promotions.domain.model.aggregates.Promotion;
import com.nexa.platform.promotions.interfaces.rest.resources.PromotionResponse;

public class PromotionResponseFromEntityAssembler {
    public static PromotionResponse toResourceFromEntity(Promotion entity) {
        return new PromotionResponse(
            entity.getPromoCode(),
            entity.getName(),
            entity.getDescription(),
            entity.getDiscountLabel(),
            entity.getVisibility(),
            entity.getStatus(),
            entity.getProductIds(),
            entity.getNotes()
        );
    }
}
