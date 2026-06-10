using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Warehouse.Domain.Model.Commands;

public record ReleaseInventoryReservationCommand(int InventoryItemId, InventoryReservation InventoryReservation);
