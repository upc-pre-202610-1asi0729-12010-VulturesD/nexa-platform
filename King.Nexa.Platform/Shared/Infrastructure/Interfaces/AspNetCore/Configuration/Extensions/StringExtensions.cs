using System.Text.RegularExpressions;

namespace King.Nexa.Platform.Shared.Infrastructure.Interfaces.AspNetCore.Configuration.Extensions;

public static partial class StringExtensions
{
    public static string ToKebabCase(this string value) =>
        KebabCaseExpression().Replace(value, "$1-$2").ToLowerInvariant();

    [GeneratedRegex("([a-z0-9])([A-Z])")]
    private static partial Regex KebabCaseExpression();
}
