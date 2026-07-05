package com.nexa.platform.logistics.application.dtos;

import java.time.OffsetDateTime;

public final class DispatchActionRequests {
    private DispatchActionRequests() { }

    public record AssignDispatchRequest(String responsible) { }
    public record ScheduleDispatchRequest(OffsetDateTime eta, String deliveryWindow, String note) { }
    public record DispatchNoteRequest(String note) { }
    public record DispatchStatusChangeRequest(String status, String note, Boolean visibleToBuyer) { }
}
