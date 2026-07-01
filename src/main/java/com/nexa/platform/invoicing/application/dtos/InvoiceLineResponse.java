package com.nexa.platform.invoicing.application.dtos;

import java.math.BigDecimal;

public record InvoiceLineResponse(Long id, String description, int quantity, BigDecimal unitPrice,
                                  BigDecimal subtotal) { }
