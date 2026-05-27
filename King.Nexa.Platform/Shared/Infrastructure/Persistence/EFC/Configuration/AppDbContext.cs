using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;
using King.Nexa.Platform.Invoicing.Domain.Model.Aggregates;
using King.Nexa.Platform.Invoicing.Domain.Model.ValueObjects;
using King.Nexa.Platform.Logistics.Domain.Model.Aggregates;
using King.Nexa.Platform.Logistics.Domain.Model.ValueObjects;
using King.Nexa.Platform.Sales.Domain.Model.Aggregates;
using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;
using King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;
using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;
using Microsoft.EntityFrameworkCore;

namespace King.Nexa.Platform.Shared.Infrastructure.Persistence.EFC.Configuration;

public class AppDbContext(DbContextOptions<AppDbContext> options) : DbContext(options)
{
    public DbSet<Order> Orders => Set<Order>();

    public DbSet<Shipment> Shipments => Set<Shipment>();

    public DbSet<InventoryItem> InventoryItems => Set<InventoryItem>();

    public DbSet<Invoice> Invoices => Set<Invoice>();

    public DbSet<Product> Products => Set<Product>();

    protected override void OnModelCreating(ModelBuilder builder)
    {
        base.OnModelCreating(builder);
        ConfigureSales(builder);
        ConfigureLogistics(builder);
        ConfigureWarehouse(builder);
        ConfigureInvoicing(builder);
        ConfigureCatalogManagement(builder);
    }

    private static void ConfigureSales(ModelBuilder builder)
    {
        builder.Entity<Order>().HasKey(order => order.Id);
        builder.Entity<Order>().Property(order => order.OrderNumber)
            .HasConversion(value => value.Value, value => new OrderNumber(value))
            .HasMaxLength(32)
            .IsRequired();
        builder.Entity<Order>().Property(order => order.CustomerId)
            .HasConversion(value => value.Value, value => new CustomerId(value))
            .HasMaxLength(64)
            .IsRequired();
        builder.Entity<Order>().Property(order => order.Status).HasConversion<string>().HasMaxLength(24).IsRequired();
        builder.Entity<Order>().HasIndex(order => order.OrderNumber).IsUnique();
    }

    private static void ConfigureLogistics(ModelBuilder builder)
    {
        builder.Entity<Shipment>().HasKey(shipment => shipment.Id);
        builder.Entity<Shipment>().Property(shipment => shipment.ShipmentCode)
            .HasConversion(value => value.Value, value => new ShipmentCode(value))
            .HasMaxLength(32)
            .IsRequired();
        builder.Entity<Shipment>().Property(shipment => shipment.Status).HasConversion<string>().HasMaxLength(24).IsRequired();
        builder.Entity<Shipment>().OwnsOne(shipment => shipment.LastTemperatureRecord);
        builder.Entity<Shipment>().HasIndex(shipment => shipment.ShipmentCode).IsUnique();
    }

    private static void ConfigureWarehouse(ModelBuilder builder)
    {
        builder.Entity<InventoryItem>().HasKey(item => item.Id);
        builder.Entity<InventoryItem>().Property(item => item.ProductCode).HasMaxLength(32).IsRequired();
        builder.Entity<InventoryItem>().Property(item => item.LotCode)
            .HasConversion(value => value.Value, value => new LotCode(value))
            .HasMaxLength(32)
            .IsRequired();
        builder.Entity<InventoryItem>().OwnsOne(item => item.StorageLocation);
        builder.Entity<InventoryItem>().OwnsOne(item => item.StockQuantity);
        builder.Entity<InventoryItem>().HasIndex(item => new { item.ProductCode, item.LotCode }).IsUnique();
    }

    private static void ConfigureInvoicing(ModelBuilder builder)
    {
        builder.Entity<Invoice>().HasKey(invoice => invoice.Id);
        builder.Entity<Invoice>().Property(invoice => invoice.InvoiceNumber)
            .HasConversion(value => value.Value, value => new InvoiceNumber(value))
            .HasMaxLength(32)
            .IsRequired();
        builder.Entity<Invoice>().OwnsOne(invoice => invoice.BillingAmount);
        builder.Entity<Invoice>().Property(invoice => invoice.PaymentStatus).HasConversion<string>().HasMaxLength(24).IsRequired();
        builder.Entity<Invoice>().HasIndex(invoice => invoice.InvoiceNumber).IsUnique();
    }

    private static void ConfigureCatalogManagement(ModelBuilder builder)
    {
        builder.Entity<Product>().HasKey(product => product.Id);
        builder.Entity<Product>().Property(product => product.ProductCode)
            .HasConversion(value => value.Value, value => new ProductCode(value))
            .HasMaxLength(32)
            .IsRequired();
        builder.Entity<Product>().Property(product => product.Name).HasMaxLength(120).IsRequired();
        builder.Entity<Product>().Property(product => product.CategoryName)
            .HasConversion(value => value.Value, value => new CategoryName(value))
            .HasMaxLength(80)
            .IsRequired();
        builder.Entity<Product>().Property(product => product.ColdChainRequirement).HasConversion<string>().HasMaxLength(32).IsRequired();
        builder.Entity<Product>().HasIndex(product => product.ProductCode).IsUnique();
    }
}
