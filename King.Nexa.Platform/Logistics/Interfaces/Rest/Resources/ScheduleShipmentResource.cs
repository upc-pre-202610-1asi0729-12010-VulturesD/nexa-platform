namespace King.Nexa.Platform.Logistics.Interfaces.Rest.Resources;

/// <summary>
/// Data required to schedule a shipment for an order.
/// </summary>
public record ScheduleShipmentResource(string ShipmentCode, int OrderId, DateTimeOffset ScheduledAt);
