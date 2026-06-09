using King.Nexa.Platform.Sales.Domain.Model.Commands;
using King.Nexa.Platform.Sales.Domain.Model.Entities;
using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;
using King.Nexa.Platform.Shared.Domain.Model.Entities;

namespace King.Nexa.Platform.Sales.Domain.Model.Aggregates;

/// <summary>
/// Aggregate root for customer orders and their order items.
/// </summary>
public class Order : AuditableEntity
{
    protected Order()
    {
        OrderNumber = null!;
        CustomerId = null!;
        Total = null!;
    }

    public Order(CreateOrderCommand command)
    {
        if (command.Items.Count == 0)
            throw new ArgumentException("An order must contain at least one item.", nameof(command));

        OrderNumber = command.OrderNumber;
        CustomerId = command.CustomerId;
        Items = command.Items.Select(item => new OrderItem(item)).ToList();
        Total = CalculateTotal(Items);
        Status = OrderStatus.Pending;
    }

    public OrderNumber OrderNumber { get; private set; }

    public CustomerId CustomerId { get; private set; }

    public OrderStatus Status { get; private set; }

    public Money Total { get; private set; }

    public PaymentConfirmation? PaymentConfirmation { get; private set; }

    public InventoryReservation? InventoryReservation { get; private set; }

    public RejectionReason? RejectionReason { get; private set; }

    public DateTimeOffset? ConfirmedAt { get; private set; }

    public List<OrderItem> Items { get; private set; } = [];

    public void Update(UpdateOrderCommand command)
    {
        if (Status != OrderStatus.Pending)
            throw new InvalidOperationException("Only pending orders can be updated.");
        if (command.Items.Count == 0)
            throw new ArgumentException("An order must contain at least one item.", nameof(command));

        CustomerId = command.CustomerId;
        Items.Clear();
        Items.AddRange(command.Items.Select(item => new OrderItem(item)));
        Total = CalculateTotal(Items);
    }

    public void Confirm(PaymentConfirmation paymentConfirmation, InventoryReservation inventoryReservation)
    {
        if (Status == OrderStatus.Cancelled) throw new InvalidOperationException("Cancelled orders cannot be confirmed.");
        if (Status == OrderStatus.Rejected) throw new InvalidOperationException("Rejected orders cannot be confirmed.");

        Status = OrderStatus.Confirmed;
        PaymentConfirmation = paymentConfirmation;
        InventoryReservation = inventoryReservation;
        ConfirmedAt = DateTimeOffset.UtcNow;
    }

    public void Reject(RejectionReason rejectionReason)
    {
        if (Status == OrderStatus.Confirmed || Status == OrderStatus.Paid)
            throw new InvalidOperationException("Confirmed or paid orders cannot be rejected.");

        Status = OrderStatus.Rejected;
        RejectionReason = rejectionReason;
    }

    public void Cancel()
    {
        if (Status == OrderStatus.Confirmed || Status == OrderStatus.Paid)
            throw new InvalidOperationException("Confirmed or paid orders cannot be cancelled.");

        Status = OrderStatus.Cancelled;
    }

    private static Money CalculateTotal(IReadOnlyCollection<OrderItem> items)
    {
        var first = items.First();
        return items.Skip(1).Aggregate(first.Subtotal, (total, item) => total.Add(item.Subtotal));
    }
}
