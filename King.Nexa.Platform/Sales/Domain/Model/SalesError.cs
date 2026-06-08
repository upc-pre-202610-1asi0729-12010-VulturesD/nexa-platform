namespace King.Nexa.Platform.Sales.Domain.Model;

public enum SalesError
{
    None,
    OrderNotFound,
    OrderItemNotFound,
    CustomerNotFound,
    OrderCreationFailed,
    OrderUpdateFailed,
    OrderCancellationFailed,
    InvalidOrderStatus,
    InvalidOrderData,
    OperationCancelled,
    DatabaseError,
    InternalServerError
}
