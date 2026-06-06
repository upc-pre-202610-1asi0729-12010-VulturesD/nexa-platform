using King.Nexa.Platform.Logistics.Domain.Model.Aggregates;
using King.Nexa.Platform.Logistics.Domain.Model.ValueObjects;
using Microsoft.EntityFrameworkCore;

namespace King.Nexa.Platform.Logistics.Infrastructure.Persistence.EntityFrameworkCore.Configuration.Extensions;

public static class ModelBuilderExtensions
{
    public static void ApplyLogisticsConfiguration(this ModelBuilder builder)
    {
        var shipment = builder.Entity<Shipment>();

        shipment.ToTable("shipments");
        shipment.HasKey(entity => entity.Id);
        shipment.Property(entity => entity.ShipmentCode)
            .HasConversion(value => value.Value, value => new ShipmentCode(value))
            .HasColumnName("shipment_code")
            .HasMaxLength(32)
            .IsRequired();
        shipment.Property(entity => entity.Status)
            .HasColumnName("status")
            .HasConversion<string>()
            .HasMaxLength(24)
            .IsRequired();
        shipment.OwnsOne(entity => entity.LastTemperatureRecord, temperature =>
        {
            temperature.Property(value => value.Celsius).HasColumnName("last_temperature_celsius").HasPrecision(5, 2);
            temperature.Property(value => value.RecordedAt).HasColumnName("last_temperature_recorded_at");
        });
        shipment.HasIndex(entity => entity.ShipmentCode).IsUnique();
    }
}
