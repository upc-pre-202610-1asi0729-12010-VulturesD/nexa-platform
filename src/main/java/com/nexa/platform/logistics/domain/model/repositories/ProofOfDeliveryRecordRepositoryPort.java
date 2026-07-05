package com.nexa.platform.logistics.domain.model.repositories;

import com.nexa.platform.logistics.domain.model.ProofOfDeliveryRecord;
import java.util.List;
import java.util.Optional;

public interface ProofOfDeliveryRecordRepositoryPort {
    List<ProofOfDeliveryRecord> findAllByOrderByIdAsc();
    List<ProofOfDeliveryRecord> findByDispatchOrderIdOrderByIdAsc(Long dispatchOrderId);
    Optional<ProofOfDeliveryRecord> findById(Long id);
    ProofOfDeliveryRecord save(ProofOfDeliveryRecord proof);
    void delete(ProofOfDeliveryRecord proof);
}
