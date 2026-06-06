namespace King.Nexa.Platform.Logistics.Interfaces.Rest.Resources;

public record ScheduleShipmentResource(string ShipmentCode, int OrderId, DateTimeOffset ScheduledAt);
