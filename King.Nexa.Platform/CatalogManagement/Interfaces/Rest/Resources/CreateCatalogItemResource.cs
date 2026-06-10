namespace King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Resources;

/// <summary>
/// Data required to publish a catalog item.
/// </summary>
public record CreateCatalogItemResource(
    string CatalogItemId,
    string ProductId,
    string ItemName,
    string BrandName,
    string CategoryName,
    string Description,
    string ImageUrl,
    decimal UnitPriceAmount,
    string UnitPriceCurrency,
    int AvailableStock,
    string ColdChainRequirement);
