package com.nexa.platform.invoicing.application.dtos;

import java.time.OffsetDateTime;

public final class NotificationRecordDtos {
    private NotificationRecordDtos() { }

    public record UpsertNotificationRecordRequest(Long clientAccountId, String recipientRole, String type,
                                                  String title, String body, boolean read) { }

    public record NotificationRecordResponse(Long id, Long tenantId, Long clientAccountId,
                                             String recipientRole, String type, String title, String body,
                                             boolean read, OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
}
