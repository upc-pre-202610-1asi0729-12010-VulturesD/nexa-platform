using System.Net;
using System.Text.Json;
using King.Nexa.Platform.Shared.Infrastructure.Localization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Localization;

namespace King.Nexa.Platform.Shared.Infrastructure.Pipeline.Middleware.Components;

public sealed class GlobalExceptionHandlerMiddleware(
    RequestDelegate next,
    ILogger<GlobalExceptionHandlerMiddleware> logger,
    IStringLocalizer<SharedResource> localizer)
{
    public async Task InvokeAsync(HttpContext context)
    {
        try
        {
            await next(context);
        }
        catch (ArgumentException exception)
        {
            await WriteProblemAsync(context, HttpStatusCode.BadRequest, localizer["InvalidRequestTitle"], exception.Message);
        }
        catch (InvalidOperationException exception)
        {
            await WriteProblemAsync(context, HttpStatusCode.Conflict, localizer["BusinessRuleViolationTitle"], exception.Message);
        }
        catch (Exception exception)
        {
            logger.LogError(exception, "Unhandled request failure.");
            await WriteProblemAsync(context, HttpStatusCode.InternalServerError, localizer["UnexpectedErrorTitle"], localizer["UnexpectedErrorDetail"]);
        }
    }

    private static async Task WriteProblemAsync(HttpContext context, HttpStatusCode statusCode, string title, string detail)
    {
        if (context.Response.HasStarted)
            return;

        context.Response.Clear();
        context.Response.StatusCode = (int)statusCode;
        context.Response.ContentType = "application/problem+json";

        var problem = new ProblemDetails
        {
            Status = (int)statusCode,
            Title = title,
            Detail = detail,
            Instance = context.Request.Path
        };

        await JsonSerializer.SerializeAsync(context.Response.Body, problem);
    }
}
