package com.nexa.platform.invoicing.application.dtos;

import java.time.OffsetDateTime;

public final class BusinessDocumentDtos {
    private BusinessDocumentDtos() { }

    public record BusinessDocumentA1Response(Long id, Long tenantId, Long orderId, Long clientAccountId,
                                             Long documentTypeId, String type, String label, String status,
                                             String fileName, boolean visibleToBuyer, boolean required,
                                             OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
    public record CreateBusinessDocumentRequest(Long tenantId, Long orderId, Long clientAccountId,
                                                Long documentTypeId, String type, String label,
                                                boolean visibleToBuyer, boolean required, String fileName) { }
    public record GenerateBusinessDocumentRequest(Long tenantId, Long orderId, String type) { }
    public record UploadBusinessDocumentRequest(Long tenantId, Long orderId, Long clientAccountId,
                                                String type, String label, boolean visibleToBuyer,
                                                boolean required, String fileName, String contentType,
                                                byte[] content) { }
    public record ChangeBusinessDocumentStatusRequest(String status, Boolean visibleToBuyer) { }
    public record BusinessDocumentContentResponse(byte[] content, String fileName, String contentType,
                                                  boolean visibleToBuyer) { }
}
