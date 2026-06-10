using King.Nexa.Platform.Iam.Domain.Model.Aggregates;

namespace King.Nexa.Platform.Iam.Application.OutboundServices;

public interface ITokenService
{
    string GenerateToken(User user);
}
