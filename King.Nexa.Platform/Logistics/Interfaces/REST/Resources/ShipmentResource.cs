namespace King.Nexa.Platform.Logistics.Interfaces.Rest.Resources;

public record ShipmentResource(int Id, string ShipmentCode, int OrderId, string Status, DateTimeOffset ScheduledAt, DateTimeOffset? DeliveredAt, decimal? LastTemperatureCelsius);
