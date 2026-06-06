using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Sales.Domain.Model.Commands;

/// <summary>
/// Command used to create an order with one or more items.
/// </summary>
public record CreateOrderCommand(OrderNumber OrderNumber, CustomerId CustomerId, IReadOnlyCollection<CreateOrderItemCommand> Items);
