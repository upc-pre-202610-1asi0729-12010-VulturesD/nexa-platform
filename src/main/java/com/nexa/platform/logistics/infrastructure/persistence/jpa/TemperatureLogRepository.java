package com.nexa.platform.logistics.infrastructure.persistence.jpa;

import com.nexa.platform.logistics.domain.model.TemperatureLog;
import com.nexa.platform.logistics.domain.model.repositories.TemperatureLogRepositoryPort;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemperatureLogRepository extends JpaRepository<TemperatureLog, Long>, TemperatureLogRepositoryPort {
    List<TemperatureLog> findAllByOrderByIdAsc();
    List<TemperatureLog> findByDispatchOrderIdOrderByIdAsc(Long dispatchOrderId);
}
