namespace King.Nexa.Platform.Iam.Domain.Model.Commands;

/// <summary>
/// Command used to register a platform user.
/// </summary>
public record SignUpCommand(string Username, string Email, string Password, string Role);
