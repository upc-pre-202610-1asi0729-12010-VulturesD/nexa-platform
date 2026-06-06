namespace King.Nexa.Platform.Sales.Interfaces.Rest.Resources;

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
