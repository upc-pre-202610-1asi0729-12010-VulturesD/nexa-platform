using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Warehouse.Domain.Model.Commands;

public record UpdateInventoryItemCommand(
    int InventoryItemId,
    ProductId ProductId,
    CatalogItemId CatalogItemId,
    StockQuantity AvailableQuantity,
    WarehouseLocation WarehouseLocation,
    TemperatureRange TemperatureRange);
