using System.Security.Cryptography;
using King.Nexa.Platform.Iam.Application.OutboundServices;
using King.Nexa.Platform.Iam.Domain.Model.Aggregates;

namespace King.Nexa.Platform.Iam.Infrastructure.Tokens;

public class TokenService : ITokenService
{
    public string GenerateToken(User user) =>
        Convert.ToBase64String(RandomNumberGenerator.GetBytes(32));
}
