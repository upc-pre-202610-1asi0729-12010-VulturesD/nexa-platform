package com.nexa.platform.logistics.infrastructure.persistence.jpa;

import com.nexa.platform.logistics.domain.model.DispatchEvent;
import com.nexa.platform.logistics.domain.model.repositories.DispatchEventRepositoryPort;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispatchEventRepository extends JpaRepository<DispatchEvent, Long>, DispatchEventRepositoryPort {
    List<DispatchEvent> findAllByOrderByIdAsc();
    List<DispatchEvent> findByDispatchOrderIdOrderByIdAsc(Long dispatchOrderId);
}
