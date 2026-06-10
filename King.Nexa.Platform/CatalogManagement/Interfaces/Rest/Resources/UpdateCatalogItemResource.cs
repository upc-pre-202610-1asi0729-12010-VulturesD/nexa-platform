namespace King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Resources;

/// <summary>
/// Data required to update a catalog item.
/// </summary>
public record UpdateCatalogItemResource(
    string ItemName,
    string BrandName,
    string CategoryName,
    string Description,
    string ImageUrl,
    decimal UnitPriceAmount,
    string UnitPriceCurrency,
    int AvailableStock,
    string ColdChainRequirement);
