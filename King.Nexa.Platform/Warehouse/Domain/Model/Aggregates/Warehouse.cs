using King.Nexa.Platform.Shared.Domain.Model.Entities;
using King.Nexa.Platform.Warehouse.Domain.Model.Commands;
using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;

public class Warehouse : AuditableEntity
{
    protected Warehouse()
    {
        Name = null!;
        Location = null!;
        TemperatureRange = null!;
    }

    public Warehouse(CreateWarehouseCommand command)
    {
        Name = command.Name;
        Location = command.Location;
        TemperatureRange = command.TemperatureRange;
        IsActive = true;
    }

    public WarehouseName Name { get; private set; }

    public WarehouseLocation Location { get; private set; }

    public TemperatureRange TemperatureRange { get; private set; }

    public bool IsActive { get; private set; }

    public void Update(UpdateWarehouseCommand command)
    {
        Name = command.Name;
        Location = command.Location;
        TemperatureRange = command.TemperatureRange;
    }

    public void Deactivate() => IsActive = false;
}
