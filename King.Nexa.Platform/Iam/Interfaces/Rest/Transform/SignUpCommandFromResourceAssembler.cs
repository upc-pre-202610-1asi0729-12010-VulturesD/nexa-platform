using King.Nexa.Platform.Iam.Domain.Model.Commands;
using King.Nexa.Platform.Iam.Interfaces.Rest.Resources;

namespace King.Nexa.Platform.Iam.Interfaces.Rest.Transform;

public static class SignUpCommandFromResourceAssembler
{
    public static SignUpCommand ToCommandFromResource(SignUpResource resource) =>
        new(resource.Username, resource.Email, resource.Password, resource.Role);
}
