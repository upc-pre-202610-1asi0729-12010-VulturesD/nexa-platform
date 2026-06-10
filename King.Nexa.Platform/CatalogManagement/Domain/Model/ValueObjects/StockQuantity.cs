namespace King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;

public sealed record StockQuantity
{
    public StockQuantity(int value)
    {
        if (value < 0) throw new ArgumentOutOfRangeException(nameof(value), "Stock quantity cannot be negative.");
        Value = value;
    }

    public int Value { get; }
}
