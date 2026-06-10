using King.Nexa.Platform.CatalogManagement.Application.Internal.CommandServices;
using King.Nexa.Platform.CatalogManagement.Application.Internal.QueryServices;
using King.Nexa.Platform.CatalogManagement.Application.CommandServices;
using King.Nexa.Platform.CatalogManagement.Application.QueryServices;
using King.Nexa.Platform.CatalogManagement.Domain.Repositories;
using King.Nexa.Platform.CatalogManagement.Infrastructure.Persistence.EntityFrameworkCore.Repositories;

namespace King.Nexa.Platform.CatalogManagement.Infrastructure.DependencyInjection;

public static class CatalogManagementServiceCollectionExtensions
{
    public static IServiceCollection AddCatalogManagement(this IServiceCollection services)
    {
        services.AddScoped<ICatalogItemRepository, CatalogItemRepository>();
        services.AddScoped<ICatalogItemCommandService, CatalogItemCommandService>();
        services.AddScoped<ICatalogItemQueryService, CatalogItemQueryService>();
        services.AddScoped<ICategoryRepository, CategoryRepository>();
        services.AddScoped<ICategoryCommandService, CategoryCommandService>();
        services.AddScoped<ICategoryQueryService, CategoryQueryService>();
        services.AddScoped<IBrandRepository, BrandRepository>();
        services.AddScoped<IBrandCommandService, BrandCommandService>();
        services.AddScoped<IBrandQueryService, BrandQueryService>();

        return services;
    }
}
