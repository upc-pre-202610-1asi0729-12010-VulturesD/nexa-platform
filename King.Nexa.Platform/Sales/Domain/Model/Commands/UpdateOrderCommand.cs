using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Sales.Domain.Model.Commands;

public record UpdateOrderCommand(int OrderId, CustomerId CustomerId, IReadOnlyCollection<CreateOrderItemCommand> Items);
