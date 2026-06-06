using King.Nexa.Platform.Shared.Infrastructure.Pipeline.Middleware.Components;

namespace King.Nexa.Platform.Shared.Infrastructure.Pipeline.Middleware.Extensions;

public static class MiddlewareExtensions
{
    public static IApplicationBuilder UseGlobalExceptionHandling(this IApplicationBuilder app) =>
        app.UseMiddleware<GlobalExceptionHandlerMiddleware>();
}
