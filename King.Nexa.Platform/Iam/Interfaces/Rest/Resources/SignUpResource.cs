namespace King.Nexa.Platform.Iam.Interfaces.Rest.Resources;

/// <summary>
/// Data required to register a user.
/// </summary>
public record SignUpResource(string Username, string Email, string Password, string Role);
