package com.nexa.platform.promotions.domain.model.repositories;

import com.nexa.platform.promotions.domain.model.aggregates.Promotion;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository {
    Promotion save(Promotion promotion);
    List<Promotion> findAll();
    List<Promotion> findByTenantIdOrderByIdAsc(Long tenantId);
    Optional<Promotion> findByIdAndTenantId(Long id, Long tenantId);
    Optional<Promotion> findByPromoCode(String promoCode);
    Optional<Promotion> findByTenantIdAndPromoCode(Long tenantId, String promoCode);
    void delete(Promotion promotion);
}
