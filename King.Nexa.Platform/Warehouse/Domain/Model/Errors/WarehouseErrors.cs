using King.Nexa.Platform.Shared.Domain.Model;

namespace King.Nexa.Platform.Warehouse.Domain.Model.Errors;

public static class WarehouseErrors
{
    public static readonly Error InventoryItemNotFound =
        new("Warehouse.InventoryItemNotFound", "The specified inventory item was not found.");

    public static readonly Error WarehouseNotFound =
        new("Warehouse.WarehouseNotFound", "The specified warehouse was not found.");

    public static readonly Error InventoryMovementNotFound =
        new("Warehouse.InventoryMovementNotFound", "The specified inventory movement was not found.");

    public static readonly Error InsufficientStock =
        new("Warehouse.InsufficientStock", "There is not enough available stock to complete the operation.");

    public static readonly Error StockReservationFailed =
        new("Warehouse.StockReservationFailed", "The stock reservation could not be completed.");

    public static readonly Error StockReleaseFailed =
        new("Warehouse.StockReleaseFailed", "The stock release could not be completed.");

    public static readonly Error InvalidInventoryData =
        new("Warehouse.InvalidInventoryData", "The supplied inventory data is invalid.");

    public static readonly Error OperationCancelled =
        new("Warehouse.OperationCancelled", "The warehouse operation was cancelled.");

    public static readonly Error DatabaseError =
        new("Warehouse.DatabaseError", "A persistence error occurred while processing warehouse data.");

    public static readonly Error InternalServerError =
        new("Warehouse.InternalServerError", "An internal server error occurred while processing the warehouse request.");
}
