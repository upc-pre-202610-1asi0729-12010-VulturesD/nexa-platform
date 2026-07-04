package com.nexa.platform.invoicing.application.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentResponse(String id, Long backendId, Long invoiceId, String invoiceCode, String orderId,
                              String referenceCode, BigDecimal amount, String currency,
                              String status, String method, Long tenantId, Long clientAccountId,
                              Long paymentMethodRecordId, String rejectionReason,
                              OffsetDateTime confirmedAt, OffsetDateTime rejectedAt,
                              OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
