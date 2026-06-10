namespace King.Nexa.Platform.Logistics.Domain.Model.ValueObjects;

public sealed record TemperatureRecord(decimal Celsius, DateTimeOffset RecordedAt)
{
    public TemperatureRecord(decimal celsius) : this(celsius, DateTimeOffset.UtcNow)
    {
    }
}
