using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Resources;

namespace King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Transform;

public static class BrandResourceFromEntityAssembler
{
    public static BrandResource ToResourceFromEntity(Brand entity) =>
        new(entity.Id, entity.Name.Value, entity.Description, entity.IsActive);
}
