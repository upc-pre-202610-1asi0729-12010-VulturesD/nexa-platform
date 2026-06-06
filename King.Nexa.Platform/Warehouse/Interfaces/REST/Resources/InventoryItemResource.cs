namespace King.Nexa.Platform.Warehouse.Interfaces.Rest.Resources;

/// <summary>
/// Inventory item response resource.
/// </summary>
public record InventoryItemResource(
    int Id,
    string ProductId,
    string CatalogItemId,
    int AvailableQuantity,
    int ReservedQuantity,
    string WarehouseLocation,
    decimal MinimumTemperature,
    decimal MaximumTemperature);
