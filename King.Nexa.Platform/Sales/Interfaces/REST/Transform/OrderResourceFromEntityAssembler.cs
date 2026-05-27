using King.Nexa.Platform.Sales.Domain.Model.Aggregates;
using King.Nexa.Platform.Sales.Interfaces.REST.Resources;

namespace King.Nexa.Platform.Sales.Interfaces.REST.Transform;

public static class OrderResourceFromEntityAssembler
{
    public static OrderResource ToResourceFromEntity(Order entity) =>
        new(entity.Id, entity.OrderNumber.Value, entity.CustomerId.Value, entity.Status.ToString(), entity.ConfirmedAt);
}
