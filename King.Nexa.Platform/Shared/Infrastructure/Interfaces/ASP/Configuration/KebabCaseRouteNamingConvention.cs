using Microsoft.AspNetCore.Mvc.ApplicationModels;
using System.Text.RegularExpressions;

namespace King.Nexa.Platform.Shared.Infrastructure.Interfaces.ASP.Configuration;

public partial class KebabCaseRouteNamingConvention : IControllerModelConvention
{
    public void Apply(ControllerModel controller)
    {
        foreach (var selector in controller.Selectors)
            selector.AttributeRouteModel = ReplaceControllerTemplate(selector, controller.ControllerName);

        foreach (var selector in controller.Actions.SelectMany(action => action.Selectors))
            selector.AttributeRouteModel = ReplaceControllerTemplate(selector, controller.ControllerName);
    }

    private static AttributeRouteModel? ReplaceControllerTemplate(SelectorModel selector, string controllerName)
    {
        if (selector.AttributeRouteModel is null) return null;

        return new AttributeRouteModel
        {
            Template = selector.AttributeRouteModel.Template?.Replace("[controller]", ToKebabCase(controllerName))
        };
    }

    private static string ToKebabCase(string value) =>
        KebabCaseExpression().Replace(value, "$1-$2").ToLowerInvariant();

    [GeneratedRegex("([a-z0-9])([A-Z])")]
    private static partial Regex KebabCaseExpression();
}
