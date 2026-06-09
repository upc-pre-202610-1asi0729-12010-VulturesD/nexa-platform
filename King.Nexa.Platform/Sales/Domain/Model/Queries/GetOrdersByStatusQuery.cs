using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Sales.Domain.Model.Queries;

public record GetOrdersByStatusQuery(OrderStatus Status);
