using King.Nexa.Platform.Shared.Domain.Model.Entities;

namespace King.Nexa.Platform.Iam.Domain.Model.Aggregates;

public class User : AuditableEntity
{
    protected User()
    {
        Username = string.Empty;
        Email = string.Empty;
        PasswordHash = string.Empty;
        Role = string.Empty;
    }

    public User(string username, string email, string passwordHash, string role)
    {
        if (string.IsNullOrWhiteSpace(username)) throw new ArgumentException("Username is required.", nameof(username));
        if (string.IsNullOrWhiteSpace(email)) throw new ArgumentException("Email is required.", nameof(email));
        if (string.IsNullOrWhiteSpace(passwordHash)) throw new ArgumentException("Password hash is required.", nameof(passwordHash));
        if (string.IsNullOrWhiteSpace(role)) throw new ArgumentException("Role is required.", nameof(role));

        Username = username.Trim();
        Email = email.Trim().ToLowerInvariant();
        PasswordHash = passwordHash;
        Role = role.Trim();
    }

    public string Username { get; private set; }

    public string Email { get; private set; }

    public string PasswordHash { get; private set; }

    public string Role { get; private set; }
}
