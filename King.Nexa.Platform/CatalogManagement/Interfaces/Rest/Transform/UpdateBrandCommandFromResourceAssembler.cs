using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;
using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;
using King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Resources;

namespace King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Transform;

public static class UpdateBrandCommandFromResourceAssembler
{
    public static UpdateBrandCommand ToCommandFromResource(int id, UpdateBrandResource resource) =>
        new(id, new BrandName(resource.Name), resource.Description);
}
