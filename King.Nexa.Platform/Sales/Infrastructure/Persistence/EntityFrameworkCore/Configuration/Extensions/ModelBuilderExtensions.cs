using King.Nexa.Platform.Sales.Domain.Model.Aggregates;
using King.Nexa.Platform.Sales.Domain.Model.Entities;
using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;
using Microsoft.EntityFrameworkCore;

namespace King.Nexa.Platform.Sales.Infrastructure.Persistence.EntityFrameworkCore.Configuration.Extensions;

public static class ModelBuilderExtensions
{
    public static void ApplySalesConfiguration(this ModelBuilder builder)
    {
        var order = builder.Entity<Order>();
        order.ToTable("orders");
        order.HasKey(entity => entity.Id);
        order.Property(entity => entity.OrderNumber)
            .HasConversion(value => value.Value, value => new OrderNumber(value))
            .HasColumnName("order_number")
            .HasMaxLength(32)
            .IsRequired();
        order.Property(entity => entity.CustomerId)
            .HasConversion(value => value.Value, value => new CustomerId(value))
            .HasColumnName("customer_id")
            .HasMaxLength(64)
            .IsRequired();
        order.Property(entity => entity.Status)
            .HasColumnName("status")
            .HasConversion<string>()
            .HasMaxLength(24)
            .IsRequired();
        order.OwnsOne(entity => entity.Total, money =>
        {
            money.Property(value => value.Amount).HasColumnName("total_amount").HasPrecision(12, 2).IsRequired();
            money.Property(value => value.Currency).HasColumnName("total_currency").HasMaxLength(3).IsRequired();
        });
        order.Property(entity => entity.PaymentConfirmation)
            .HasConversion(value => value == null ? null : value.Value, value => value == null ? null : new PaymentConfirmation(value))
            .HasColumnName("payment_confirmation")
            .HasMaxLength(120);
        order.Property(entity => entity.InventoryReservation)
            .HasConversion(value => value == null ? null : value.Value, value => value == null ? null : new InventoryReservation(value))
            .HasColumnName("inventory_reservation")
            .HasMaxLength(120);
        order.Property(entity => entity.RejectionReason)
            .HasConversion(value => value == null ? null : value.Value, value => value == null ? null : new RejectionReason(value))
            .HasColumnName("rejection_reason")
            .HasMaxLength(240);
        order.HasIndex(entity => entity.OrderNumber).IsUnique();
        order.HasMany(entity => entity.Items)
            .WithOne()
            .HasForeignKey(item => item.OrderId)
            .OnDelete(DeleteBehavior.Cascade);

        var orderItem = builder.Entity<OrderItem>();
        orderItem.ToTable("order_items");
        orderItem.HasKey(entity => entity.Id);
        orderItem.Property(entity => entity.ProductId)
            .HasConversion(value => value.Value, value => new ProductId(value))
            .HasColumnName("product_id")
            .HasMaxLength(64)
            .IsRequired();
        orderItem.Property(entity => entity.CatalogItemId)
            .HasConversion(value => value.Value, value => new CatalogItemId(value))
            .HasColumnName("catalog_item_id")
            .HasMaxLength(64)
            .IsRequired();
        orderItem.Property(entity => entity.ItemName)
            .HasConversion(value => value.Value, value => new ItemName(value))
            .HasColumnName("item_name")
            .HasMaxLength(160)
            .IsRequired();
        orderItem.Property(entity => entity.Quantity)
            .HasConversion(value => value.Value, value => new Quantity(value))
            .HasColumnName("quantity")
            .IsRequired();
        orderItem.OwnsOne(entity => entity.UnitPrice, money =>
        {
            money.Property(value => value.Amount).HasColumnName("unit_price_amount").HasPrecision(12, 2).IsRequired();
            money.Property(value => value.Currency).HasColumnName("unit_price_currency").HasMaxLength(3).IsRequired();
        });
        orderItem.OwnsOne(entity => entity.Subtotal, money =>
        {
            money.Property(value => value.Amount).HasColumnName("subtotal_amount").HasPrecision(12, 2).IsRequired();
            money.Property(value => value.Currency).HasColumnName("subtotal_currency").HasMaxLength(3).IsRequired();
        });
    }
}
