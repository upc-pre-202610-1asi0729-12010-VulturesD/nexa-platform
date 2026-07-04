package com.nexa.platform.logistics.domain.model.repositories;

import com.nexa.platform.logistics.domain.model.DeliveryRoute;
import java.util.Optional;

public interface DeliveryRouteRepositoryPort {
    Optional<DeliveryRoute> findById(Long id);
    java.util.List<DeliveryRoute> findAll();
    DeliveryRoute save(DeliveryRoute route);
}
