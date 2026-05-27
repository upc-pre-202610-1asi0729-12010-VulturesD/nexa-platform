using King.Nexa.Platform.Sales.Domain.Model.Commands;
using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;
using King.Nexa.Platform.Shared.Domain.Model;

namespace King.Nexa.Platform.Sales.Domain.Model.Aggregates;

public class Order : AuditableEntity
{
    protected Order()
    {
        OrderNumber = null!;
        CustomerId = null!;
    }

    public Order(CreateOrderCommand command)
    {
        OrderNumber = command.OrderNumber;
        CustomerId = command.CustomerId;
        Status = OrderStatus.Draft;
    }

    public OrderNumber OrderNumber { get; private set; }

    public CustomerId CustomerId { get; private set; }

    public OrderStatus Status { get; private set; }

    public DateTimeOffset? ConfirmedAt { get; private set; }

    public void Confirm()
    {
        if (Status == OrderStatus.Cancelled) throw new InvalidOperationException("Cancelled orders cannot be confirmed.");
        Status = OrderStatus.Confirmed;
        ConfirmedAt = DateTimeOffset.UtcNow;
    }
}
