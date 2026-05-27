using King.Nexa.Platform.Sales.Domain.Model.Commands;
using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;
using King.Nexa.Platform.Sales.Interfaces.REST.Resources;

namespace King.Nexa.Platform.Sales.Interfaces.REST.Transform;

public static class CreateOrderCommandFromResourceAssembler
{
    public static CreateOrderCommand ToCommandFromResource(CreateOrderResource resource) =>
        new(new OrderNumber(resource.OrderNumber), new CustomerId(resource.CustomerId));
}
