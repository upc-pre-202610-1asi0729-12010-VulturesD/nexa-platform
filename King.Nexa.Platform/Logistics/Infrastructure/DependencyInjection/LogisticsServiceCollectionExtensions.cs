using King.Nexa.Platform.Logistics.Application.Internal.CommandServices;
using King.Nexa.Platform.Logistics.Application.Internal.QueryServices;
using King.Nexa.Platform.Logistics.Application.CommandServices;
using King.Nexa.Platform.Logistics.Application.QueryServices;
using King.Nexa.Platform.Logistics.Domain.Repositories;
using King.Nexa.Platform.Logistics.Infrastructure.Persistence.EntityFrameworkCore.Repositories;

namespace King.Nexa.Platform.Logistics.Infrastructure.DependencyInjection;

public static class LogisticsServiceCollectionExtensions
{
    public static IServiceCollection AddLogistics(this IServiceCollection services)
    {
        services.AddScoped<IShipmentRepository, ShipmentRepository>();
        services.AddScoped<IShipmentCommandService, ShipmentCommandService>();
        services.AddScoped<IShipmentQueryService, ShipmentQueryService>();

        return services;
    }
}
