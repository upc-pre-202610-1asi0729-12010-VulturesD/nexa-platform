package com.nexa.platform.logistics.domain.model.repositories;

import com.nexa.platform.logistics.domain.model.DispatchEvent;
import java.util.List;
import java.util.Optional;

public interface DispatchEventRepositoryPort {
    List<DispatchEvent> findAllByOrderByIdAsc();
    List<DispatchEvent> findByDispatchOrderIdOrderByIdAsc(Long dispatchOrderId);
    Optional<DispatchEvent> findById(Long id);
    DispatchEvent save(DispatchEvent event);
    void delete(DispatchEvent event);
}
