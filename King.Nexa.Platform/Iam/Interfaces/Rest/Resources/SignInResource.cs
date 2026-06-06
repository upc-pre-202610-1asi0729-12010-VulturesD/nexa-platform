namespace King.Nexa.Platform.Iam.Interfaces.Rest.Resources;

/// <summary>
/// Data required to sign in.
/// </summary>
public record SignInResource(string Username, string Password);
