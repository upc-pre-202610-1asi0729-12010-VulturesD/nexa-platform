using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Infrastructure.Persistence.EntityFrameworkCore.Configuration.Extensions;
using King.Nexa.Platform.Iam.Domain.Model.Aggregates;
using King.Nexa.Platform.Iam.Infrastructure.Persistence.EntityFrameworkCore.Configuration.Extensions;
using King.Nexa.Platform.Invoicing.Domain.Model.Aggregates;
using King.Nexa.Platform.Invoicing.Infrastructure.Persistence.EntityFrameworkCore.Configuration.Extensions;
using King.Nexa.Platform.Logistics.Domain.Model.Aggregates;
using King.Nexa.Platform.Logistics.Infrastructure.Persistence.EntityFrameworkCore.Configuration.Extensions;
using King.Nexa.Platform.Sales.Domain.Model.Aggregates;
using King.Nexa.Platform.Sales.Domain.Model.Entities;
using King.Nexa.Platform.Sales.Infrastructure.Persistence.EntityFrameworkCore.Configuration.Extensions;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Configuration.Extensions;
using King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;
using King.Nexa.Platform.Warehouse.Infrastructure.Persistence.EntityFrameworkCore.Configuration.Extensions;
using Microsoft.EntityFrameworkCore;

namespace King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Configuration;

public class AppDbContext(DbContextOptions<AppDbContext> options) : DbContext(options)
{
    public DbSet<CatalogItem> CatalogItems => Set<CatalogItem>();

    public DbSet<Order> Orders => Set<Order>();

    public DbSet<OrderItem> OrderItems => Set<OrderItem>();

    public DbSet<InventoryItem> InventoryItems => Set<InventoryItem>();

    public DbSet<Shipment> Shipments => Set<Shipment>();

    public DbSet<Invoice> Invoices => Set<Invoice>();

    public DbSet<User> Users => Set<User>();

    protected override void OnModelCreating(ModelBuilder builder)
    {
        base.OnModelCreating(builder);

        builder.ApplyCatalogManagementConfiguration();
        builder.ApplySalesConfiguration();
        builder.ApplyWarehouseConfiguration();
        builder.ApplyLogisticsConfiguration();
        builder.ApplyInvoicingConfiguration();
        builder.ApplyIamConfiguration();

        builder.UseSnakeCasePluralNamingConvention();
    }
}
