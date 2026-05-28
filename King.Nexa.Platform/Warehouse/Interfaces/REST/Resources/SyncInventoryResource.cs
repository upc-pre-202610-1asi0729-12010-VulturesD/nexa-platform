namespace King.Nexa.Platform.Warehouse.Interfaces.REST.Resources;

public record SyncInventoryResource(string ProductCode, string LotCode, string Zone, string Rack, int AvailableUnits);
