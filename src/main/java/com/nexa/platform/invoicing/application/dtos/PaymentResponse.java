package com.nexa.platform.invoicing.application.dtos;

import java.math.BigDecimal;

public record PaymentResponse(String id, Long backendId, Long invoiceId, String invoiceCode, String orderId,
                              String referenceCode, BigDecimal amount, String currency,
                              String status, String method) { }
