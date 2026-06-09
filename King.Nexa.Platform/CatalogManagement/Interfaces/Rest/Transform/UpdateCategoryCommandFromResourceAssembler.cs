using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;
using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;
using King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Resources;

namespace King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Transform;

public static class UpdateCategoryCommandFromResourceAssembler
{
    public static UpdateCategoryCommand ToCommandFromResource(int id, UpdateCategoryResource resource) =>
        new(id, new CategoryName(resource.Name), resource.Description);
}
