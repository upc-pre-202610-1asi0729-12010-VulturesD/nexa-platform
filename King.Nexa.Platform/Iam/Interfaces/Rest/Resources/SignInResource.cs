namespace King.Nexa.Platform.Iam.Interfaces.Rest.Resources;

/// <summary>
/// Data required to sign in.
/// </summary>
/// <param name="Email">The email address of the user (preferred).</param>
/// <param name="Username">The username of the user (backward compatibility).</param>
/// <param name="Password">The user's password.</param>
public record SignInResource(string? Email, string? Username, string Password);
