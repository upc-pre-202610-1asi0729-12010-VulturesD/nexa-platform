namespace King.Nexa.Platform.Sales.Interfaces.REST.Resources;

public record OrderResource(int Id, string OrderNumber, string CustomerId, string Status, DateTimeOffset? ConfirmedAt);
