using King.Nexa.Platform.Iam.Domain.Model.Commands;
using King.Nexa.Platform.Iam.Interfaces.Rest.Resources;

namespace King.Nexa.Platform.Iam.Interfaces.Rest.Transform;

public static class SignInCommandFromResourceAssembler
{
    public static SignInCommand ToCommandFromResource(SignInResource resource) =>
        new(resource.Email ?? resource.Username ?? string.Empty, resource.Password);
}
