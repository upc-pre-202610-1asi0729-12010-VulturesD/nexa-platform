using King.Nexa.Platform.Shared.Domain.Repositories;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Configuration;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Interceptors;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Repositories;
using King.Nexa.Platform.Shared.Infrastructure.Seed;
using Microsoft.EntityFrameworkCore;
using System.Data.Common;

namespace King.Nexa.Platform.Shared.Infrastructure.DependencyInjection;

public static class SharedServiceCollectionExtensions
{
    public static IServiceCollection AddSharedInfrastructure(this IServiceCollection services, IConfiguration configuration)
    {
        services.AddProblemDetails();
        services.Configure<SeedDataOptions>(configuration.GetSection("SeedData"));
        services.AddScoped<AuditableEntityInterceptor>();
        services.AddDbContext<AppDbContext>((serviceProvider, options) =>
        {
            var connectionStringTemplate = configuration.GetConnectionString("DefaultConnection");
            if (string.IsNullOrWhiteSpace(connectionStringTemplate))
                throw new InvalidOperationException("Connection string 'DefaultConnection' is not configured.");

            var connectionString = Environment.ExpandEnvironmentVariables(connectionStringTemplate);
            var environment = serviceProvider.GetRequiredService<IHostEnvironment>();
            var loggerFactory = serviceProvider.GetRequiredService<ILoggerFactory>();

            options.UseMySql(connectionString, ServerVersion.AutoDetect(connectionString))
                .UseLoggerFactory(loggerFactory)
                .EnableDetailedErrors()
                .AddInterceptors(serviceProvider.GetRequiredService<AuditableEntityInterceptor>());

            if (environment.IsDevelopment())
            {
                options.EnableSensitiveDataLogging();
                LogSafeDatabaseTarget(
                    connectionString,
                    serviceProvider.GetRequiredService<ILoggerFactory>()
                        .CreateLogger(nameof(SharedServiceCollectionExtensions)));
            }
        });

        services.AddScoped(typeof(IBaseRepository<>), typeof(BaseRepository<>));
        services.AddScoped<IUnitOfWork, UnitOfWork>();
        services.AddScoped<ISeedDataService, SeedDataService>();

        return services;
    }

    private static void LogSafeDatabaseTarget(string connectionString, ILogger logger)
    {
        var builder = new DbConnectionStringBuilder { ConnectionString = connectionString };
        var server = GetConnectionStringValue(builder, "server", "host", "data source") ?? "configured server";
        var database = GetConnectionStringValue(builder, "database", "initial catalog") ?? "configured database";

        logger.LogInformation("Database configured for server {Server}, database {Database}.", server, database);
    }

    private static string? GetConnectionStringValue(DbConnectionStringBuilder builder, params string[] keys)
    {
        foreach (var key in keys)
        {
            if (builder.TryGetValue(key, out var value) && value is not null)
                return value.ToString();
        }

        return null;
    }
}
