using King.Nexa.Platform.Logistics.Domain.Model.Commands;
using King.Nexa.Platform.Logistics.Interfaces.Rest.Resources;

namespace King.Nexa.Platform.Logistics.Interfaces.Rest.Transform;

public static class RescheduleShipmentCommandFromResourceAssembler
{
    public static RescheduleShipmentCommand ToCommandFromResource(int id, RescheduleShipmentResource resource) =>
        new(id, resource.ScheduledAt);
}
