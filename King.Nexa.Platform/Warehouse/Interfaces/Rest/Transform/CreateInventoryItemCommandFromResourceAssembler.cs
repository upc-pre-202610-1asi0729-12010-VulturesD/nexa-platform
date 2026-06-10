using King.Nexa.Platform.Warehouse.Domain.Model.Commands;
using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;
using King.Nexa.Platform.Warehouse.Interfaces.Rest.Resources;

namespace King.Nexa.Platform.Warehouse.Interfaces.Rest.Transform;

public static class CreateInventoryItemCommandFromResourceAssembler
{
    public static CreateInventoryItemCommand ToCommandFromResource(CreateInventoryItemResource resource) =>
        new(
            new ProductId(resource.ProductId),
            new CatalogItemId(resource.CatalogItemId),
            new StockQuantity(resource.AvailableQuantity),
            new WarehouseLocation(resource.WarehouseLocation),
            new TemperatureRange(resource.MinimumTemperature, resource.MaximumTemperature));
}
