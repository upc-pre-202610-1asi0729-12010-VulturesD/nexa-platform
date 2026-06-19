package com.nexa.platform.promotions.infrastructure.persistence.jpa;

import com.nexa.platform.promotions.domain.model.aggregates.Promotion;
import com.nexa.platform.promotions.domain.model.repositories.PromotionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PromotionPersistenceAdapter implements PromotionRepository {
    private final SpringDataPromotionJpaRepository repository;

    public PromotionPersistenceAdapter(SpringDataPromotionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Promotion save(Promotion promotion) {
        return repository.save(promotion);
    }

    @Override
    public List<Promotion> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Promotion> findByPromoCode(String promoCode) {
        return repository.findByPromoCode(promoCode);
    }
}
