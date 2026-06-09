using King.Nexa.Platform.Sales.Domain.Model.Commands;
using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;
using King.Nexa.Platform.Sales.Interfaces.Rest.Resources;

namespace King.Nexa.Platform.Sales.Interfaces.Rest.Transform;

public static class UpdateOrderCommandFromResourceAssembler
{
    public static UpdateOrderCommand ToCommandFromResource(int id, UpdateOrderResource resource) =>
        new(
            id,
            new CustomerId(resource.CustomerId),
            resource.Items.Select(item => new CreateOrderItemCommand(
                new ProductId(item.ProductId),
                new CatalogItemId(item.CatalogItemId),
                new ItemName(item.ItemName),
                new Quantity(item.Quantity),
                new Money(item.UnitPriceAmount, item.UnitPriceCurrency))).ToList());
}
