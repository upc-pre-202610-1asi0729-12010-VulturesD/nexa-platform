using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Resources;

namespace King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Transform;

public static class CatalogItemResourceFromEntityAssembler
{
    public static CatalogItemResource ToResourceFromEntity(CatalogItem entity) =>
        new(
            entity.Id,
            entity.CatalogItemId.Value,
            entity.ProductId.Value,
            entity.ItemName.Value,
            entity.BrandName.Value,
            entity.CategoryName.Value,
            entity.Description,
            entity.UnitPrice.Amount,
            entity.UnitPrice.Currency,
            entity.AvailableStock.Value,
            entity.ColdChainRequirement.ToString(),
            entity.IsActive);
}
