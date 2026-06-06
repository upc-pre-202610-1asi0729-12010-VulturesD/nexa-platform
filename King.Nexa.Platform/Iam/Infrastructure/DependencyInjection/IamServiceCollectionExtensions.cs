using King.Nexa.Platform.Iam.Domain.Repositories;
using King.Nexa.Platform.Iam.Infrastructure.Persistence.EntityFrameworkCore.Repositories;

namespace King.Nexa.Platform.Iam.Infrastructure.DependencyInjection;

public static class IamServiceCollectionExtensions
{
    public static IServiceCollection AddIam(this IServiceCollection services)
    {
        services.AddScoped<IUserRepository, UserRepository>();

        return services;
    }
}
