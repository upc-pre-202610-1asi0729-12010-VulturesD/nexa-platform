using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;

/// <summary>
/// Command used to create a catalog item.
/// </summary>
public record CreateCatalogItemCommand(
    CatalogItemId CatalogItemId,
    ProductId ProductId,
    ItemName ItemName,
    BrandName BrandName,
    CategoryName CategoryName,
    string Description,
    string ImageUrl,
    Money UnitPrice,
    StockQuantity AvailableStock,
    ColdChainRequirement ColdChainRequirement);
