namespace King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;

/// <summary>
/// Non-negative stock quantity value object.
/// </summary>
public sealed record StockQuantity
{
    public StockQuantity(int value)
    {
        if (value < 0) throw new ArgumentOutOfRangeException(nameof(value), "Stock quantity cannot be negative.");
        Value = value;
    }

    public int Value { get; }
}
