package com.nexa.platform.promotions.application.internal.queryservices;

import com.nexa.platform.promotions.application.queries.GetAllPromotionsQuery;
import com.nexa.platform.promotions.application.queryservices.PromotionQueryService;
import com.nexa.platform.promotions.domain.model.aggregates.Promotion;
import com.nexa.platform.promotions.domain.model.repositories.PromotionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PromotionQueryServiceImpl implements PromotionQueryService {
    private final PromotionRepository repository;

    public PromotionQueryServiceImpl(PromotionRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Promotion> handle(GetAllPromotionsQuery query) {
        return repository.findAll();
    }
}
