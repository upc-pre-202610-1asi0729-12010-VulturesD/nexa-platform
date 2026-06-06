namespace King.Nexa.Platform.Sales.Interfaces.Rest.Resources;

/// <summary>
/// Data required to create an order.
/// </summary>
public record CreateOrderResource(string OrderNumber, string CustomerId, IReadOnlyCollection<CreateOrderItemResource> Items);
