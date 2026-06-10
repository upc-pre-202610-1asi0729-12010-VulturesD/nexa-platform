using King.Nexa.Platform.Warehouse.Domain.Model.Commands;
using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;
using King.Nexa.Platform.Warehouse.Interfaces.Rest.Resources;

namespace King.Nexa.Platform.Warehouse.Interfaces.Rest.Transform;

public static class CreateWarehouseCommandFromResourceAssembler
{
    public static CreateWarehouseCommand ToCommandFromResource(CreateWarehouseResource resource) =>
        new(
            new WarehouseName(resource.Name),
            new WarehouseLocation(resource.Location),
            new TemperatureRange(resource.MinimumTemperature, resource.MaximumTemperature));
}
