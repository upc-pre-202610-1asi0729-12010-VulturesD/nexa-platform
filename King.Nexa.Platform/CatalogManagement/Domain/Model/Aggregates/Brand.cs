using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;
using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;
using King.Nexa.Platform.Shared.Domain.Model.Entities;

namespace King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;

public class Brand : AuditableEntity
{
    protected Brand()
    {
        Name = null!;
        Description = string.Empty;
    }

    public Brand(CreateBrandCommand command)
    {
        Name = command.Name;
        Description = command.Description.Trim();
        IsActive = true;
    }

    public BrandName Name { get; private set; }

    public string Description { get; private set; }

    public bool IsActive { get; private set; }

    public void Update(UpdateBrandCommand command)
    {
        Name = command.Name;
        Description = command.Description.Trim();
    }

    public void Deactivate() => IsActive = false;
}
