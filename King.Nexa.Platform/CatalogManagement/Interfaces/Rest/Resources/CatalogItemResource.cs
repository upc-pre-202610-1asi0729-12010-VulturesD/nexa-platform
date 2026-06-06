namespace King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Resources;

/// <summary>
/// Published product information exposed by the catalog.
/// </summary>
public record CatalogItemResource(
    int Id,
    string CatalogItemId,
    string ProductId,
    string ItemName,
    string BrandName,
    string CategoryName,
    string Description,
    decimal UnitPriceAmount,
    string UnitPriceCurrency,
    int AvailableStock,
    string ColdChainRequirement,
    bool IsActive);
