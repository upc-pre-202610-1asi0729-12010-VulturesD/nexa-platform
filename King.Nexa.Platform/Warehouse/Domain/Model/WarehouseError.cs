namespace King.Nexa.Platform.Warehouse.Domain.Model;

public enum WarehouseError
{
    None,
    InventoryItemNotFound,
    WarehouseNotFound,
    InventoryMovementNotFound,
    InsufficientStock,
    StockReservationFailed,
    StockReleaseFailed,
    InvalidInventoryData,
    OperationCancelled,
    DatabaseError,
    InternalServerError
}
