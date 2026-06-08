using King.Nexa.Platform.Shared.Domain.Model;

namespace King.Nexa.Platform.Sales.Domain.Model.Errors;

public static class SalesErrors
{
    public static readonly Error OrderNotFound =
        new("Sales.OrderNotFound", "The specified order was not found.");

    public static readonly Error OrderItemNotFound =
        new("Sales.OrderItemNotFound", "The specified order item was not found.");

    public static readonly Error CustomerNotFound =
        new("Sales.CustomerNotFound", "The specified B2B customer was not found.");

    public static readonly Error OrderCreationFailed =
        new("Sales.OrderCreationFailed", "An error occurred while creating the order.");

    public static readonly Error OrderUpdateFailed =
        new("Sales.OrderUpdateFailed", "An error occurred while updating the order.");

    public static readonly Error OrderCancellationFailed =
        new("Sales.OrderCancellationFailed", "An error occurred while cancelling the order.");

    public static readonly Error InvalidOrderStatus =
        new("Sales.InvalidOrderStatus", "The order status is invalid for the requested operation.");

    public static readonly Error InvalidOrderData =
        new("Sales.InvalidOrderData", "The supplied order data is invalid.");

    public static readonly Error OperationCancelled =
        new("Sales.OperationCancelled", "The sales operation was cancelled.");

    public static readonly Error DatabaseError =
        new("Sales.DatabaseError", "A persistence error occurred while processing sales data.");

    public static readonly Error InternalServerError =
        new("Sales.InternalServerError", "An internal server error occurred while processing the sales request.");
}
