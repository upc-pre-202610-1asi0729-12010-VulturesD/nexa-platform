namespace King.Nexa.Platform.Sales.Interfaces.Rest.Resources;

/// <summary>
/// Data required to add one item to an order.
/// </summary>
public record CreateOrderItemResource(
    string ProductId,
    string CatalogItemId,
    string ItemName,
    int Quantity,
    decimal UnitPriceAmount,
    string UnitPriceCurrency);
