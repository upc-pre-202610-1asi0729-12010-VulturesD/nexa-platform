package com.nexa.platform.invoicing.application.dtos;

import java.math.BigDecimal;

public record PaymentResponse(Long id, Long invoiceId, BigDecimal amount, String method) { }
