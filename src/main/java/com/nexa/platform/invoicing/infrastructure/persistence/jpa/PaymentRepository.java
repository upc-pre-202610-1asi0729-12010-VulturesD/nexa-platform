package com.nexa.platform.invoicing.infrastructure.persistence.jpa;

import com.nexa.platform.invoicing.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> { }
