using Microsoft.AspNetCore.Http;

namespace King.Nexa.Platform.Shared.Interfaces.Rest.ProblemDetails;

public static class ProblemDetailsFactory
{
    public static Microsoft.AspNetCore.Mvc.ProblemDetails Create(int status, string title, string detail, PathString? instance = null) =>
        new()
        {
            Status = status,
            Title = title,
            Detail = detail,
            Instance = instance?.Value
        };
}
