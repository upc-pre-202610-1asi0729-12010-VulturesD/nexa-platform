package com.nexa.platform.invoicing.application.dtos;

import java.math.BigDecimal;

public record BusinessDocumentResponse(String id, String clientId, String orderId, String type, String label,
                                       String fileName, String status, boolean visibleToBuyer,
                                       boolean required, String dueDate, BigDecimal amount) { }
