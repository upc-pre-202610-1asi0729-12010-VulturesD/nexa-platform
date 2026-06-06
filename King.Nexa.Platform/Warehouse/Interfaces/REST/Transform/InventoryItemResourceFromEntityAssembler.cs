using King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;
using King.Nexa.Platform.Warehouse.Interfaces.Rest.Resources;

namespace King.Nexa.Platform.Warehouse.Interfaces.Rest.Transform;

public static class InventoryItemResourceFromEntityAssembler
{
    public static InventoryItemResource ToResourceFromEntity(InventoryItem entity) =>
        new(
            entity.Id,
            entity.ProductId.Value,
            entity.CatalogItemId.Value,
            entity.AvailableQuantity.Value,
            entity.ReservedQuantity.Value,
            entity.WarehouseLocation.Value,
            entity.TemperatureRange.MinimumTemperature,
            entity.TemperatureRange.MaximumTemperature);
}
