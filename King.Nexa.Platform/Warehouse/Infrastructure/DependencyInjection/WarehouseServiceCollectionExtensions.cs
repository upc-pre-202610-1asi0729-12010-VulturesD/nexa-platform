using King.Nexa.Platform.Warehouse.Application.Internal.CommandServices;
using King.Nexa.Platform.Warehouse.Application.Internal.QueryServices;
using King.Nexa.Platform.Warehouse.Application.CommandServices;
using King.Nexa.Platform.Warehouse.Application.QueryServices;
using King.Nexa.Platform.Warehouse.Domain.Repositories;
using King.Nexa.Platform.Warehouse.Infrastructure.Persistence.EntityFrameworkCore.Repositories;

namespace King.Nexa.Platform.Warehouse.Infrastructure.DependencyInjection;

public static class WarehouseServiceCollectionExtensions
{
    public static IServiceCollection AddWarehouse(this IServiceCollection services)
    {
        services.AddScoped<IInventoryItemRepository, InventoryItemRepository>();
        services.AddScoped<IInventoryItemCommandService, InventoryItemCommandService>();
        services.AddScoped<IInventoryItemQueryService, InventoryItemQueryService>();

        return services;
    }
}
