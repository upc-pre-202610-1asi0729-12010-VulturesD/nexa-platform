namespace King.Nexa.Platform.Sales.Domain.Model.ValueObjects;

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

    public Money Multiply(int multiplier) => new(Amount * multiplier, Currency);

    public Money Add(Money other)
    {
        if (!string.Equals(Currency, other.Currency, StringComparison.OrdinalIgnoreCase))
            throw new InvalidOperationException("Cannot add money values with different currencies.");

        return new Money(Amount + other.Amount, Currency);
    }
}
