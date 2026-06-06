namespace King.Nexa.Platform.Sales.Domain.Model.ValueObjects;

/// <summary>
/// Positive quantity value object.
/// </summary>
public sealed record Quantity
{
    public Quantity(int value)
    {
        if (value <= 0) throw new ArgumentException("Quantity must be greater than zero.", nameof(value));
        Value = value;
    }

    public int Value { get; }
}
