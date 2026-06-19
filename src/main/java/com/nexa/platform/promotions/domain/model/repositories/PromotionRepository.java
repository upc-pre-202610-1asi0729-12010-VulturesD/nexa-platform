package com.nexa.platform.promotions.domain.model.repositories;

import com.nexa.platform.promotions.domain.model.aggregates.Promotion;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository {
    Promotion save(Promotion promotion);
    List<Promotion> findAll();
    Optional<Promotion> findByPromoCode(String promoCode);
}
