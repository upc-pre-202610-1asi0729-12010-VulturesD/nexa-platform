using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;
using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;
using King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Resources;

namespace King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Transform;

public static class CreateBrandCommandFromResourceAssembler
{
    public static CreateBrandCommand ToCommandFromResource(CreateBrandResource resource) =>
        new(new BrandName(resource.Name), resource.Description);
}
