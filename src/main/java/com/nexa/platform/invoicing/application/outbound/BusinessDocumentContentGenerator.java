package com.nexa.platform.invoicing.application.outbound;

import com.nexa.platform.sales.domain.model.SalesOrder;

public interface BusinessDocumentContentGenerator {
    GeneratedBusinessDocumentContent generate(SalesOrder order, String type);

    record GeneratedBusinessDocumentContent(byte[] content, String fileName, String contentType,
                                            String label, Long clientAccountId) { }
}
