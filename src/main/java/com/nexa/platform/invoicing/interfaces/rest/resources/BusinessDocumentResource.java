package com.nexa.platform.invoicing.interfaces.rest.resources;

import java.math.BigDecimal;

public record BusinessDocumentResource(String id, String clientId, String orderId, String type, String label,
                                       String fileName, String status, boolean visibleToBuyer,
                                       boolean required, String dueDate, BigDecimal amount) { }
