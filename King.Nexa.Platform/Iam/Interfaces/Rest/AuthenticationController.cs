using King.Nexa.Platform.Iam.Application.CommandServices;
using King.Nexa.Platform.Iam.Interfaces.Rest.Resources;
using King.Nexa.Platform.Iam.Interfaces.Rest.Transform;
using Microsoft.AspNetCore.Mvc;

namespace King.Nexa.Platform.Iam.Interfaces.Rest;

[ApiController]
[Route("api/v1/authentication")]
public class AuthenticationController(IUserCommandService userCommandService) : ControllerBase
{
    /// <summary>
    /// Registers a new platform user and returns a session token.
    /// </summary>
    [HttpPost("sign-up")]
    public async Task<IActionResult> SignUp(SignUpResource resource, CancellationToken cancellationToken)
    {
        var command = SignUpCommandFromResourceAssembler.ToCommandFromResource(resource);
        var authenticatedUser = await userCommandService.SignUpAsync(command, cancellationToken);

        return Created(string.Empty, AuthenticatedUserResourceFromEntityAssembler.ToResourceFromEntity(authenticatedUser));
    }

    /// <summary>
    /// Authenticates a platform user with username and password.
    /// </summary>
    [HttpPost("sign-in")]
    public async Task<IActionResult> SignIn(SignInResource resource, CancellationToken cancellationToken)
    {
        if (string.IsNullOrWhiteSpace(resource.Email) && string.IsNullOrWhiteSpace(resource.Username))
        {
            return BadRequest(new { message = "Username or Email is required." });
        }

        var command = SignInCommandFromResourceAssembler.ToCommandFromResource(resource);
        var authenticatedUser = await userCommandService.SignInAsync(command, cancellationToken);

        return authenticatedUser is null
            ? Unauthorized()
            : Ok(AuthenticatedUserResourceFromEntityAssembler.ToResourceFromEntity(authenticatedUser));
    }
}
