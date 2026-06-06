using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;
using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;
using King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Resources;

namespace King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Transform;

public static class CreateCatalogItemCommandFromResourceAssembler
{
    public static CreateCatalogItemCommand ToCommandFromResource(CreateCatalogItemResource resource) =>
        new(
            new CatalogItemId(resource.CatalogItemId),
            new ProductId(resource.ProductId),
            new ItemName(resource.ItemName),
            new BrandName(resource.BrandName),
            new CategoryName(resource.CategoryName),
            resource.Description,
            new Money(resource.UnitPriceAmount, resource.UnitPriceCurrency),
            new StockQuantity(resource.AvailableStock),
            Enum.Parse<ColdChainRequirement>(resource.ColdChainRequirement, true));
}
