package com.nexa.platform.invoicing.domain.model.repositories;

import com.nexa.platform.invoicing.domain.model.Invoice;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepositoryPort {
    Optional<Invoice> findById(Long id);
    List<Invoice> findAll();
    Invoice save(Invoice invoice);
}
