namespace King.Nexa.Platform.Sales.Interfaces.Rest.Resources;

/// <summary>
/// Order response resource.
/// </summary>
public record OrderResource(
    int Id,
    string OrderNumber,
    string CustomerId,
    string Status,
    decimal TotalAmount,
    string TotalCurrency,
    string? PaymentConfirmation,
    string? InventoryReservation,
    string? RejectionReason,
    DateTimeOffset? ConfirmedAt,
    IReadOnlyCollection<OrderItemResource> Items);
