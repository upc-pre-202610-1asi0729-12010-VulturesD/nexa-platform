namespace King.Nexa.Platform.Sales.Domain.Model.ValueObjects;

public sealed record RejectionReason
{
    public RejectionReason(string value)
    {
        if (string.IsNullOrWhiteSpace(value)) throw new ArgumentException("Rejection reason is required.", nameof(value));
        Value = value.Trim();
    }

    public string Value { get; }

    public override string ToString() => Value;
}
