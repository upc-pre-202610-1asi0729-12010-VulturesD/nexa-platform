using King.Nexa.Platform.Shared.Domain.Repositories;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EFC.Configuration;

namespace King.Nexa.Platform.Shared.Infrastructure.Persistence.EFC.Repositories;

public class UnitOfWork(AppDbContext context) : IUnitOfWork
{
    public async Task CompleteAsync(CancellationToken cancellationToken = default) =>
        await context.SaveChangesAsync(cancellationToken);
}
