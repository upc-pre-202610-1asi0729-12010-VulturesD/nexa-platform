using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;
using Microsoft.EntityFrameworkCore;

namespace King.Nexa.Platform.CatalogManagement.Infrastructure.Persistence.EntityFrameworkCore.Configuration.Extensions;

public static class ModelBuilderExtensions
{
    public static void ApplyCatalogManagementConfiguration(this ModelBuilder builder)
    {
        var catalogItem = builder.Entity<CatalogItem>();

        catalogItem.ToTable("catalog_items");
        catalogItem.HasKey(item => item.Id);
        catalogItem.Property(item => item.CatalogItemId)
            .HasConversion(value => value.Value, value => new CatalogItemId(value))
            .HasColumnName("catalog_item_id")
            .HasMaxLength(64)
            .IsRequired();
        catalogItem.Property(item => item.ProductId)
            .HasConversion(value => value.Value, value => new ProductId(value))
            .HasColumnName("product_id")
            .HasMaxLength(64)
            .IsRequired();
        catalogItem.Property(item => item.ItemName)
            .HasConversion(value => value.Value, value => new ItemName(value))
            .HasColumnName("item_name")
            .HasMaxLength(160)
            .IsRequired();
        catalogItem.Property(item => item.BrandName)
            .HasConversion(value => value.Value, value => new BrandName(value))
            .HasColumnName("brand_name")
            .HasMaxLength(120)
            .IsRequired();
        catalogItem.Property(item => item.CategoryName)
            .HasConversion(value => value.Value, value => new CategoryName(value))
            .HasColumnName("category_name")
            .HasMaxLength(80)
            .IsRequired();
        catalogItem.Property(item => item.Description)
            .HasColumnName("description")
            .HasMaxLength(500)
            .IsRequired();
        catalogItem.OwnsOne(item => item.UnitPrice, money =>
        {
            money.Property(value => value.Amount).HasColumnName("unit_price_amount").HasPrecision(12, 2).IsRequired();
            money.Property(value => value.Currency).HasColumnName("unit_price_currency").HasMaxLength(3).IsRequired();
        });
        catalogItem.Property(item => item.AvailableStock)
            .HasConversion(value => value.Value, value => new StockQuantity(value))
            .HasColumnName("available_stock")
            .IsRequired();
        catalogItem.Property(item => item.ColdChainRequirement)
            .HasColumnName("cold_chain_requirement")
            .HasConversion<string>()
            .HasMaxLength(32)
            .IsRequired();
        catalogItem.Property(item => item.IsActive).HasColumnName("is_active").IsRequired();
        catalogItem.HasIndex(item => item.CatalogItemId).IsUnique();
    }
}
