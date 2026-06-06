namespace King.Nexa.Platform.Warehouse.Interfaces.Rest.Resources;

/// <summary>
/// Data required to create an inventory item.
/// </summary>
public record CreateInventoryItemResource(
    string ProductId,
    string CatalogItemId,
    int AvailableQuantity,
    string WarehouseLocation,
    decimal MinimumTemperature,
    decimal MaximumTemperature);
