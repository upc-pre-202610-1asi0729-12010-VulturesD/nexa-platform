namespace King.Nexa.Platform.Warehouse.Domain.Model.Commands;

public record ReserveInventoryCommand(int InventoryItemId, int Units);
