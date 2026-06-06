using King.Nexa.Platform.Iam.Domain.Model.Aggregates;
using King.Nexa.Platform.Iam.Domain.Model.Queries;

namespace King.Nexa.Platform.Iam.Application.QueryServices;

public interface IUserQueryService
{
    Task<User?> Handle(GetUserByUsernameQuery query, CancellationToken cancellationToken = default);
}
