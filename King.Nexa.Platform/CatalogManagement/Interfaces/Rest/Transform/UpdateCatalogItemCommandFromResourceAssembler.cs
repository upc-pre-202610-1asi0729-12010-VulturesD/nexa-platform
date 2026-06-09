using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;
using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;
using King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Resources;

namespace King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Transform;

public static class UpdateCatalogItemCommandFromResourceAssembler
{
    public static UpdateCatalogItemCommand ToCommandFromResource(int id, UpdateCatalogItemResource resource) =>
        new(
            id,
            new ItemName(resource.ItemName),
            new BrandName(resource.BrandName),
            new CategoryName(resource.CategoryName),
            resource.Description,
            resource.ImageUrl,
            new Money(resource.UnitPriceAmount, resource.UnitPriceCurrency),
            new StockQuantity(resource.AvailableStock),
            Enum.Parse<ColdChainRequirement>(resource.ColdChainRequirement, true));
}
