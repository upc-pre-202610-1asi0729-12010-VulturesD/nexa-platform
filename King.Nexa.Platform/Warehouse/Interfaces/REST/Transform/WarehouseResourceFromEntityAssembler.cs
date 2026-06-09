using King.Nexa.Platform.Warehouse.Interfaces.Rest.Resources;
using WarehouseAggregate = King.Nexa.Platform.Warehouse.Domain.Model.Aggregates.Warehouse;

namespace King.Nexa.Platform.Warehouse.Interfaces.Rest.Transform;

public static class WarehouseResourceFromEntityAssembler
{
    public static WarehouseResource ToResourceFromEntity(WarehouseAggregate entity) =>
        new(
            entity.Id,
            entity.Name.Value,
            entity.Location.Value,
            entity.TemperatureRange.MinimumTemperature,
            entity.TemperatureRange.MaximumTemperature,
            entity.IsActive);
}
