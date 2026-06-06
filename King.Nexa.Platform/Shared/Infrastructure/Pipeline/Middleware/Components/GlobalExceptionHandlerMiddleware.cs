using System.Net;
using System.Text.Json;
using Microsoft.AspNetCore.Mvc;

namespace King.Nexa.Platform.Shared.Infrastructure.Pipeline.Middleware.Components;

public sealed class GlobalExceptionHandlerMiddleware(RequestDelegate next, ILogger<GlobalExceptionHandlerMiddleware> logger)
{
    public async Task InvokeAsync(HttpContext context)
    {
        try
        {
            await next(context);
        }
        catch (ArgumentException exception)
        {
            await WriteProblemAsync(context, HttpStatusCode.BadRequest, "Invalid request.", exception.Message);
        }
        catch (InvalidOperationException exception)
        {
            await WriteProblemAsync(context, HttpStatusCode.Conflict, "Business rule violation.", exception.Message);
        }
        catch (Exception exception)
        {
            logger.LogError(exception, "Unhandled request failure.");
            await WriteProblemAsync(context, HttpStatusCode.InternalServerError, "Unexpected error.", "An unexpected server error occurred.");
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
