using King.Nexa.Platform.Iam.Application.QueryServices;
using King.Nexa.Platform.Iam.Domain.Model.Aggregates;
using King.Nexa.Platform.Iam.Domain.Model.Queries;
using King.Nexa.Platform.Iam.Domain.Repositories;

namespace King.Nexa.Platform.Iam.Application.Internal.QueryServices;

public class UserQueryService(IUserRepository userRepository) : IUserQueryService
{
    public async Task<User?> Handle(GetUserByUsernameQuery query, CancellationToken cancellationToken = default) =>
        await userRepository.FindByUsernameAsync(query.Username, cancellationToken);
}
