using King.Nexa.Platform.Iam.Application.Model;
using King.Nexa.Platform.Iam.Interfaces.Rest.Resources;

namespace King.Nexa.Platform.Iam.Interfaces.Rest.Transform;

public static class AuthenticatedUserResourceFromEntityAssembler
{
    /// <summary>
    /// Maps an authenticated user result to its REST resource.
    /// </summary>
    public static AuthenticatedUserResource ToResourceFromEntity(AuthenticatedUser authenticatedUser) =>
        new(
            authenticatedUser.User.Id,
            authenticatedUser.User.Username,
            authenticatedUser.User.Email,
            authenticatedUser.User.Role,
            authenticatedUser.AccessToken);
}
