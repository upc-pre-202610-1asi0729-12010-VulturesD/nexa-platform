package com.nexa.platform.promotions.application.queryservices;

import com.nexa.platform.promotions.domain.model.aggregates.Promotion;
import com.nexa.platform.promotions.application.queries.GetAllPromotionsQuery;
import java.util.List;

public interface PromotionQueryService {
    List<Promotion> handle(GetAllPromotionsQuery query);
}
