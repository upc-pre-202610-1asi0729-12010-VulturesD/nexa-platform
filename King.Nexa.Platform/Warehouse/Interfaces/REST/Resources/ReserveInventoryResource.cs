namespace King.Nexa.Platform.Warehouse.Interfaces.Rest.Resources;

/// <summary>
/// Data required to reserve or release inventory units.
/// </summary>
public record ReserveInventoryResource(string ReservationCode, int Units);
