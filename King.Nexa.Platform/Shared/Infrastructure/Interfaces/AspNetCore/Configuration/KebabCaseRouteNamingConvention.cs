using Microsoft.AspNetCore.Mvc.ApplicationModels;
using King.Nexa.Platform.Shared.Infrastructure.Interfaces.AspNetCore.Configuration.Extensions;

namespace King.Nexa.Platform.Shared.Infrastructure.Interfaces.AspNetCore.Configuration;

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
            Template = selector.AttributeRouteModel.Template?.Replace("[controller]", controllerName.ToKebabCase())
        };
    }
}
