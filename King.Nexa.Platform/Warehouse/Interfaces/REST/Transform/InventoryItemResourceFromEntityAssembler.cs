using King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;
using King.Nexa.Platform.Warehouse.Interfaces.REST.Resources;

namespace King.Nexa.Platform.Warehouse.Interfaces.REST.Transform;

public static class InventoryItemResourceFromEntityAssembler
{
    public static InventoryItemResource ToResourceFromEntity(InventoryItem entity) =>
        new(entity.Id, entity.ProductCode, entity.LotCode.Value, entity.StorageLocation.Label, entity.StockQuantity.AvailableUnits, entity.StockQuantity.ReservedUnits);
}
