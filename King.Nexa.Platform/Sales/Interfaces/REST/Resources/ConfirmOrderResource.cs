namespace King.Nexa.Platform.Sales.Interfaces.Rest.Resources;

/// <summary>
/// Data required to confirm an order in the sales workflow.
/// </summary>
public record ConfirmOrderResource(string PaymentConfirmation, string InventoryReservation);
