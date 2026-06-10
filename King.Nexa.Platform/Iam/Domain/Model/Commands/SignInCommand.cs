namespace King.Nexa.Platform.Iam.Domain.Model.Commands;

/// <summary>
/// Command used to authenticate a platform user.
/// </summary>
public record SignInCommand(string Username, string Password);
