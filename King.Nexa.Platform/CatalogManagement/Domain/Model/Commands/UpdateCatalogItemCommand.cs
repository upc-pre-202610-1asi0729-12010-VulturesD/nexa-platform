using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;

public record UpdateCatalogItemCommand(
    int Id,
    ItemName ItemName,
    BrandName BrandName,
    CategoryName CategoryName,
    string Description,
    string ImageUrl,
    Money UnitPrice,
    StockQuantity AvailableStock,
    ColdChainRequirement ColdChainRequirement);
