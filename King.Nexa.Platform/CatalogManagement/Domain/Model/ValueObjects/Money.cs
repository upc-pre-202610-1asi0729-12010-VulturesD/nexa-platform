namespace King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;

/// <summary>
/// Non-negative monetary value with currency.
/// </summary>
public sealed record Money
{
    public Money(decimal amount, string currency)
    {
        if (amount < 0) throw new ArgumentOutOfRangeException(nameof(amount), "Money amount cannot be negative.");
        if (string.IsNullOrWhiteSpace(currency)) throw new ArgumentException("Currency is required.", nameof(currency));

        Amount = amount;
        Currency = currency.Trim().ToUpperInvariant();
    }

    public decimal Amount { get; }

    public string Currency { get; }
}
