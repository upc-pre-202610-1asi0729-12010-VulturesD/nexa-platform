using King.Nexa.Platform.Logistics.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Logistics.Domain.Model.Commands;

public record ScheduleShipmentCommand(ShipmentCode ShipmentCode, int OrderId, DateTimeOffset ScheduledAt);
