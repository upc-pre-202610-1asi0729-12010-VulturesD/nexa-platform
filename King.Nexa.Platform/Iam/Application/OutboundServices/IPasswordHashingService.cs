namespace King.Nexa.Platform.Iam.Application.OutboundServices;

public interface IPasswordHashingService
{
    string HashPassword(string password);

    bool VerifyPassword(string password, string passwordHash);
}
