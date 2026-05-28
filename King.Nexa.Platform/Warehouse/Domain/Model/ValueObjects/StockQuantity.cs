namespace King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;

public sealed record StockQuantity
{
    public StockQuantity(int availableUnits, int reservedUnits = 0)
    {
        if (availableUnits < 0) throw new ArgumentOutOfRangeException(nameof(availableUnits));
        if (reservedUnits < 0) throw new ArgumentOutOfRangeException(nameof(reservedUnits));
        AvailableUnits = availableUnits;
        ReservedUnits = reservedUnits;
    }

    public int AvailableUnits { get; }

    public int ReservedUnits { get; }

    public StockQuantity Reserve(int units)
    {
        if (units <= 0) throw new ArgumentOutOfRangeException(nameof(units));
        if (units > AvailableUnits) throw new InvalidOperationException("Requested units exceed available stock.");
        return new StockQuantity(AvailableUnits - units, ReservedUnits + units);
    }
}
