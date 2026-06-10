using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Sales.Domain.Model.Commands;

public record CreateOrderItemCommand(
    ProductId ProductId,
    CatalogItemId CatalogItemId,
    ItemName ItemName,
    Quantity Quantity,
    Money UnitPrice);
