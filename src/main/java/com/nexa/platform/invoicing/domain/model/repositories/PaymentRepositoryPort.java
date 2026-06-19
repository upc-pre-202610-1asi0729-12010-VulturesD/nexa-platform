package com.nexa.platform.invoicing.domain.model.repositories;

import com.nexa.platform.invoicing.domain.model.Payment;
import java.util.List;

public interface PaymentRepositoryPort {
    List<Payment> findAll();
    Payment save(Payment payment);
}
