using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Warehouse.Domain.Model.Commands;

/// <summary>
/// Command used to create an inventory item.
/// </summary>
public record CreateInventoryItemCommand(
    ProductId ProductId,
    CatalogItemId CatalogItemId,
    StockQuantity AvailableQuantity,
    WarehouseLocation WarehouseLocation,
    TemperatureRange TemperatureRange);
