namespace King.Nexa.Platform.Warehouse.Interfaces.Rest.Resources;

/// <summary>
/// Data required to update an inventory item.
/// </summary>
public record UpdateInventoryItemResource(
    string ProductId,
    string CatalogItemId,
    int AvailableQuantity,
    string WarehouseLocation,
    decimal MinimumTemperature,
    decimal MaximumTemperature);
