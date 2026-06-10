namespace King.Nexa.Platform.Logistics.Interfaces.Rest.Resources;

/// <summary>
/// Data required to reschedule a shipment.
/// </summary>
public record RescheduleShipmentResource(DateTimeOffset ScheduledAt);
