namespace King.Nexa.Platform.Sales.Interfaces.Rest.Resources;

/// <summary>
/// Order item response resource with pricing information.
/// </summary>
public record OrderItemResource(
    int Id,
    string ProductId,
    string CatalogItemId,
    string ItemName,
    int Quantity,
    decimal UnitPriceAmount,
    string UnitPriceCurrency,
    decimal SubtotalAmount,
    string SubtotalCurrency);
