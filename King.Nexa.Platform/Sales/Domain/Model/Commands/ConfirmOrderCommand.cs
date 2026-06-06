using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Sales.Domain.Model.Commands;

public record ConfirmOrderCommand(int OrderId, PaymentConfirmation PaymentConfirmation, InventoryReservation InventoryReservation);
