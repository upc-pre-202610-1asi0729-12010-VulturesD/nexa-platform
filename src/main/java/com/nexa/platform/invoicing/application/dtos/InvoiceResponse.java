package com.nexa.platform.invoicing.application.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record InvoiceResponse(Long id, Long tenantId, String invoiceNumber, Long orderId, String currency,
                              String status, BigDecimal total, OffsetDateTime paidAt,
                              List<InvoiceLineResponse> lines) { }
