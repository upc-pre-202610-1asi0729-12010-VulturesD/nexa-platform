namespace King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;

public sealed record InventoryReservation
{
    public InventoryReservation(string reservationCode, int units)
    {
        if (string.IsNullOrWhiteSpace(reservationCode)) throw new ArgumentException("Reservation code is required.", nameof(reservationCode));
        if (units <= 0) throw new ArgumentException("Reserved units must be greater than zero.", nameof(units));

        ReservationCode = reservationCode.Trim().ToUpperInvariant();
        Units = units;
    }

    public string ReservationCode { get; }

    public int Units { get; }
}
