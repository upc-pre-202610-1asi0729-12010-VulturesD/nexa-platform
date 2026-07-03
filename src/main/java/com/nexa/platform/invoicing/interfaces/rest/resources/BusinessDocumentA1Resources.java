package com.nexa.platform.invoicing.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public final class BusinessDocumentA1Resources {
    private BusinessDocumentA1Resources() { }

    public record BusinessDocumentA1Resource(Long id, Long tenantId, Long orderId, Long clientAccountId,
                                             Long documentTypeId, String type, String label, String status,
                                             String fileName, boolean visibleToBuyer, boolean required,
                                             OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
    public record CreateBusinessDocumentResource(Long tenantId, Long orderId, Long clientAccountId,
                                                 Long documentTypeId, @NotBlank String type, @NotBlank String label,
                                                 boolean visibleToBuyer, boolean required, String fileName) { }
    public record GenerateBusinessDocumentResource(Long tenantId, @NotNull Long orderId, @NotBlank String type) { }
    public record ChangeBusinessDocumentStatusResource(@NotBlank String status, Boolean visibleToBuyer) { }
}
