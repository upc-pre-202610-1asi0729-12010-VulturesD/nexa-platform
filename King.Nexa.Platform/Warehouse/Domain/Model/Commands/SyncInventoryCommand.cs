using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Warehouse.Domain.Model.Commands;

public record SyncInventoryCommand(string ProductCode, LotCode LotCode, StorageLocation StorageLocation, StockQuantity StockQuantity);
