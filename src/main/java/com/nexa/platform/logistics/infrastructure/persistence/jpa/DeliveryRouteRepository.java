package com.nexa.platform.logistics.infrastructure.persistence.jpa;

import com.nexa.platform.logistics.domain.model.DeliveryRoute;
import com.nexa.platform.logistics.domain.model.repositories.DeliveryRouteRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRouteRepository extends JpaRepository<DeliveryRoute, Long>, DeliveryRouteRepositoryPort { }
