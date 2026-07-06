package com.nexa.platform.logistics.domain.model.repositories;

import com.nexa.platform.logistics.domain.model.TemperatureLog;
import java.util.List;
import java.util.Optional;

public interface TemperatureLogRepositoryPort {
    List<TemperatureLog> findAllByOrderByIdAsc();
    List<TemperatureLog> findByDispatchOrderIdOrderByIdAsc(Long dispatchOrderId);
    Optional<TemperatureLog> findById(Long id);
    TemperatureLog save(TemperatureLog log);
    void delete(TemperatureLog log);
}
