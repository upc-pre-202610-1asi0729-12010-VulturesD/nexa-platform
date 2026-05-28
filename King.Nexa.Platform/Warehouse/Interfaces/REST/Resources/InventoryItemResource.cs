namespace King.Nexa.Platform.Warehouse.Interfaces.REST.Resources;

public record InventoryItemResource(int Id, string ProductCode, string LotCode, string StorageLocation, int AvailableUnits, int ReservedUnits);
