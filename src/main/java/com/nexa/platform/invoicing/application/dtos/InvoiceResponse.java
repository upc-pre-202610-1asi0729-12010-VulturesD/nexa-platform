package com.nexa.platform.invoicing.application.dtos;

import java.math.BigDecimal;

public record InvoiceResponse(Long id, String invoiceNumber, Long orderId, String status, BigDecimal total) { }
