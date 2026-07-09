package com.nexa.platform.promotions.application.queryservices;

import com.nexa.platform.promotions.domain.model.aggregates.Promotion;
import com.nexa.platform.promotions.application.queries.GetAllPromotionsQuery;
import java.util.List;
import java.util.Optional;

public interface PromotionQueryService {
    List<Promotion> handle(GetAllPromotionsQuery query);
    List<Promotion> list(Long tenantId);
    Optional<Promotion> get(Long tenantId, Long id);
}
