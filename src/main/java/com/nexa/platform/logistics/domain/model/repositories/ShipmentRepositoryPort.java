package com.nexa.platform.logistics.domain.model.repositories;

import com.nexa.platform.logistics.domain.model.Shipment;
import java.util.List;
import java.util.Optional;

public interface ShipmentRepositoryPort {
    Optional<Shipment> findById(Long id);
    List<Shipment> findAll();
    Shipment save(Shipment shipment);
}
