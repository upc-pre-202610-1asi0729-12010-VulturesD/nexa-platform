namespace King.Nexa.Platform.Logistics.Interfaces.Rest.Resources;

/// <summary>
/// Shipment response resource with delivery status and temperature snapshot.
/// </summary>
public record ShipmentResource(int Id, string ShipmentCode, int OrderId, string Status, DateTimeOffset ScheduledAt, DateTimeOffset? DeliveredAt, decimal? LastTemperatureCelsius);
