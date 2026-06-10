namespace King.Nexa.Platform.Logistics.Domain.Model.Commands;

public record RescheduleShipmentCommand(int ShipmentId, DateTimeOffset ScheduledAt);
