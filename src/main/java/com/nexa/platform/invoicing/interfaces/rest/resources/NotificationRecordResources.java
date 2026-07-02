package com.nexa.platform.invoicing.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;

public final class NotificationRecordResources {
    private NotificationRecordResources() { }

    public record UpsertNotificationRecordResource(Long clientAccountId, String recipientRole, String type,
                                                   @NotBlank String title, String body, boolean read) { }

    public record NotificationRecordResource(Long id, Long tenantId, Long clientAccountId,
                                             String recipientRole, String type, String title, String body,
                                             boolean read, OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
}
