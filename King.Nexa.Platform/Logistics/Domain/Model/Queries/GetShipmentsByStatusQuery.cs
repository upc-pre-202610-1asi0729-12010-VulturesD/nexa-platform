using King.Nexa.Platform.Logistics.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Logistics.Domain.Model.Queries;

public record GetShipmentsByStatusQuery(DeliveryStatus Status);
