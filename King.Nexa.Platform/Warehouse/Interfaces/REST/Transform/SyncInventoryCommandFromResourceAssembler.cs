using King.Nexa.Platform.Warehouse.Domain.Model.Commands;
using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;
using King.Nexa.Platform.Warehouse.Interfaces.REST.Resources;

namespace King.Nexa.Platform.Warehouse.Interfaces.REST.Transform;

public static class SyncInventoryCommandFromResourceAssembler
{
    public static SyncInventoryCommand ToCommandFromResource(SyncInventoryResource resource) =>
        new(resource.ProductCode, new LotCode(resource.LotCode), new StorageLocation(resource.Zone, resource.Rack), new StockQuantity(resource.AvailableUnits));
}
