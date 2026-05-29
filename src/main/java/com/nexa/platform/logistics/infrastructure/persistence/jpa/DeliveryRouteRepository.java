package com.nexa.platform.logistics.infrastructure.persistence.jpa;

import com.nexa.platform.logistics.domain.model.DeliveryRoute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRouteRepository extends JpaRepository<DeliveryRoute, Long> { }
