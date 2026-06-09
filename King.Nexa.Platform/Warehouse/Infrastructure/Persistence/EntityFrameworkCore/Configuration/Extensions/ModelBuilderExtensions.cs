using King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;
using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;
using Microsoft.EntityFrameworkCore;
using WarehouseAggregate = King.Nexa.Platform.Warehouse.Domain.Model.Aggregates.Warehouse;

namespace King.Nexa.Platform.Warehouse.Infrastructure.Persistence.EntityFrameworkCore.Configuration.Extensions;

public static class ModelBuilderExtensions
{
    public static void ApplyWarehouseConfiguration(this ModelBuilder builder)
    {
        var inventoryItem = builder.Entity<InventoryItem>();

        inventoryItem.ToTable("inventory_items");
        inventoryItem.HasKey(item => item.Id);
        inventoryItem.Property(item => item.ProductId)
            .HasConversion(value => value.Value, value => new ProductId(value))
            .HasColumnName("product_id")
            .HasMaxLength(64)
            .IsRequired();
        inventoryItem.Property(item => item.CatalogItemId)
            .HasConversion(value => value.Value, value => new CatalogItemId(value))
            .HasColumnName("catalog_item_id")
            .HasMaxLength(64)
            .IsRequired();
        inventoryItem.Property(item => item.AvailableQuantity)
            .HasConversion(value => value.Value, value => new StockQuantity(value))
            .HasColumnName("available_quantity")
            .IsRequired();
        inventoryItem.Property(item => item.ReservedQuantity)
            .HasConversion(value => value.Value, value => new StockQuantity(value))
            .HasColumnName("reserved_quantity")
            .IsRequired();
        inventoryItem.Property(item => item.WarehouseLocation)
            .HasConversion(value => value.Value, value => new WarehouseLocation(value))
            .HasColumnName("warehouse_location")
            .HasMaxLength(120)
            .IsRequired();
        inventoryItem.OwnsOne(item => item.TemperatureRange, temperature =>
        {
            temperature.Property(value => value.MinimumTemperature).HasColumnName("minimum_temperature").HasPrecision(6, 2);
            temperature.Property(value => value.MaximumTemperature).HasColumnName("maximum_temperature").HasPrecision(6, 2);
        });
        inventoryItem.HasIndex(item => item.CatalogItemId).IsUnique();

        var warehouse = builder.Entity<WarehouseAggregate>();
        warehouse.ToTable("warehouses");
        warehouse.HasKey(entity => entity.Id);
        warehouse.Property(entity => entity.Name)
            .HasConversion(value => value.Value, value => new WarehouseName(value))
            .HasColumnName("name")
            .HasMaxLength(120)
            .IsRequired();
        warehouse.Property(entity => entity.Location)
            .HasConversion(value => value.Value, value => new WarehouseLocation(value))
            .HasColumnName("location")
            .HasMaxLength(120)
            .IsRequired();
        warehouse.OwnsOne(entity => entity.TemperatureRange, temperature =>
        {
            temperature.Property(value => value.MinimumTemperature).HasColumnName("minimum_temperature").HasPrecision(6, 2);
            temperature.Property(value => value.MaximumTemperature).HasColumnName("maximum_temperature").HasPrecision(6, 2);
        });
        warehouse.Property(entity => entity.IsActive).HasColumnName("is_active").IsRequired();
        warehouse.HasIndex(entity => entity.Location).IsUnique();
    }
}
