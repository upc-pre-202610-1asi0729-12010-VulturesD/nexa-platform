using King.Nexa.Platform.CatalogManagement.Application.Internal.CommandServices;
using King.Nexa.Platform.CatalogManagement.Application.Internal.QueryServices;
using King.Nexa.Platform.CatalogManagement.Application.Services;
using King.Nexa.Platform.Invoicing.Application.Internal.CommandServices;
using King.Nexa.Platform.Invoicing.Application.Internal.QueryServices;
using King.Nexa.Platform.Invoicing.Application.Services;
using King.Nexa.Platform.Logistics.Application.Internal.CommandServices;
using King.Nexa.Platform.Logistics.Application.Internal.QueryServices;
using King.Nexa.Platform.Logistics.Application.Services;
using King.Nexa.Platform.Sales.Application.Internal.CommandServices;
using King.Nexa.Platform.Sales.Application.Internal.QueryServices;
using King.Nexa.Platform.Sales.Application.Services;
using King.Nexa.Platform.Warehouse.Application.Internal.CommandServices;
using King.Nexa.Platform.Warehouse.Application.Internal.QueryServices;
using King.Nexa.Platform.Warehouse.Application.Services;

namespace King.Nexa.Platform.Shared.Infrastructure.DependencyInjection;

public static class PlatformServiceCollectionExtensions
{
    public static IServiceCollection AddPlatformApplicationServices(this IServiceCollection services)
    {
        services.AddScoped<IOrderCommandService, OrderCommandService>();
        services.AddScoped<IOrderQueryService, OrderQueryService>();
        services.AddScoped<IShipmentCommandService, ShipmentCommandService>();
        services.AddScoped<IShipmentQueryService, ShipmentQueryService>();
        services.AddScoped<IInventoryItemCommandService, InventoryItemCommandService>();
        services.AddScoped<IInventoryItemQueryService, InventoryItemQueryService>();
        services.AddScoped<IInvoiceCommandService, InvoiceCommandService>();
        services.AddScoped<IInvoiceQueryService, InvoiceQueryService>();
        services.AddScoped<IProductCommandService, ProductCommandService>();
        services.AddScoped<IProductQueryService, ProductQueryService>();

        return services;
    }
}
