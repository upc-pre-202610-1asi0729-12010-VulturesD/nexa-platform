package com.nexa.platform.invoicing.infrastructure.persistence.jpa;

import com.nexa.platform.invoicing.domain.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> { }
