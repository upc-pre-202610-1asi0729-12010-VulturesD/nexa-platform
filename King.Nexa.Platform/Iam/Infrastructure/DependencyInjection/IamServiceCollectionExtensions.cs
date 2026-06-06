using King.Nexa.Platform.Iam.Application.CommandServices;
using King.Nexa.Platform.Iam.Application.Internal.CommandServices;
using King.Nexa.Platform.Iam.Application.Internal.QueryServices;
using King.Nexa.Platform.Iam.Application.OutboundServices;
using King.Nexa.Platform.Iam.Application.QueryServices;
using King.Nexa.Platform.Iam.Domain.Repositories;
using King.Nexa.Platform.Iam.Infrastructure.Hashing;
using King.Nexa.Platform.Iam.Infrastructure.Persistence.EntityFrameworkCore.Repositories;
using King.Nexa.Platform.Iam.Infrastructure.Tokens;

namespace King.Nexa.Platform.Iam.Infrastructure.DependencyInjection;

public static class IamServiceCollectionExtensions
{
    public static IServiceCollection AddIam(this IServiceCollection services)
    {
        services.AddScoped<IUserRepository, UserRepository>();
        services.AddScoped<IUserCommandService, UserCommandService>();
        services.AddScoped<IUserQueryService, UserQueryService>();
        services.AddScoped<IPasswordHashingService, PasswordHashingService>();
        services.AddScoped<ITokenService, TokenService>();

        return services;
    }
}
