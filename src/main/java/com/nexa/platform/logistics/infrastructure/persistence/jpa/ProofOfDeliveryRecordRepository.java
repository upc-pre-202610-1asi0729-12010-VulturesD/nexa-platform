package com.nexa.platform.logistics.infrastructure.persistence.jpa;

import com.nexa.platform.logistics.domain.model.ProofOfDeliveryRecord;
import com.nexa.platform.logistics.domain.model.repositories.ProofOfDeliveryRecordRepositoryPort;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProofOfDeliveryRecordRepository extends JpaRepository<ProofOfDeliveryRecord, Long>, ProofOfDeliveryRecordRepositoryPort {
    List<ProofOfDeliveryRecord> findAllByOrderByIdAsc();
    List<ProofOfDeliveryRecord> findByDispatchOrderIdOrderByIdAsc(Long dispatchOrderId);
}
