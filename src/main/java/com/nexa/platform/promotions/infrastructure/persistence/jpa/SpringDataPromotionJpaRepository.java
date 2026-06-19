package com.nexa.platform.promotions.infrastructure.persistence.jpa;

import com.nexa.platform.promotions.domain.model.aggregates.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataPromotionJpaRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByPromoCode(String promoCode);
}
