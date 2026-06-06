namespace King.Nexa.Platform.Iam.Interfaces.Rest.Resources;

/// <summary>
/// Authenticated user response.
/// </summary>
public record AuthenticatedUserResource(int Id, string Username, string Email, string Role, string AccessToken);
