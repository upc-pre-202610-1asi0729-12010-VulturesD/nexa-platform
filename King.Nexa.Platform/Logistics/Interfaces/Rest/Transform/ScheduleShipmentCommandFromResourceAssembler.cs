using King.Nexa.Platform.Logistics.Domain.Model.Commands;
using King.Nexa.Platform.Logistics.Domain.Model.ValueObjects;
using King.Nexa.Platform.Logistics.Interfaces.Rest.Resources;

namespace King.Nexa.Platform.Logistics.Interfaces.Rest.Transform;

public static class ScheduleShipmentCommandFromResourceAssembler
{
    public static ScheduleShipmentCommand ToCommandFromResource(ScheduleShipmentResource resource) =>
        new(new ShipmentCode(resource.ShipmentCode), resource.OrderId, resource.ScheduledAt);
}
