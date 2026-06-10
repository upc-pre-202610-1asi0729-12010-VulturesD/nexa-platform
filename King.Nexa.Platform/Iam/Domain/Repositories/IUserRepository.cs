using King.Nexa.Platform.Iam.Domain.Model.Aggregates;
using King.Nexa.Platform.Shared.Domain.Repositories;

namespace King.Nexa.Platform.Iam.Domain.Repositories;

public interface IUserRepository : IBaseRepository<User>
{
    Task<User?> FindByUsernameAsync(string username, CancellationToken cancellationToken = default);
}
