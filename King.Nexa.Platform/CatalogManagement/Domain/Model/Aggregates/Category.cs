using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;
using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;
using King.Nexa.Platform.Shared.Domain.Model.Entities;

namespace King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;

public class Category : AuditableEntity
{
    protected Category()
    {
        Name = null!;
        Description = string.Empty;
    }

    public Category(CreateCategoryCommand command)
    {
        Name = command.Name;
        Description = command.Description.Trim();
        IsActive = true;
    }

    public CategoryName Name { get; private set; }

    public string Description { get; private set; }

    public bool IsActive { get; private set; }

    public void Update(UpdateCategoryCommand command)
    {
        Name = command.Name;
        Description = command.Description.Trim();
    }

    public void Deactivate() => IsActive = false;
}
