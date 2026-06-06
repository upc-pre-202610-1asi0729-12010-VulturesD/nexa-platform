namespace King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;

public sealed record TemperatureRange
{
    public TemperatureRange(decimal minimumTemperature, decimal maximumTemperature)
    {
        if (minimumTemperature > maximumTemperature)
            throw new ArgumentException("Minimum temperature cannot be greater than maximum temperature.");

        MinimumTemperature = minimumTemperature;
        MaximumTemperature = maximumTemperature;
    }

    public decimal MinimumTemperature { get; }

    public decimal MaximumTemperature { get; }
}
