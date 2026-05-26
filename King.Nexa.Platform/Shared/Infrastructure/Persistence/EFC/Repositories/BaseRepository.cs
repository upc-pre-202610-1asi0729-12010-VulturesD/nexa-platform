using King.Nexa.Platform.Shared.Domain.Repositories;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EFC.Configuration;
using Microsoft.EntityFrameworkCore;

namespace King.Nexa.Platform.Shared.Infrastructure.Persistence.EFC.Repositories;

public class BaseRepository<TEntity>(AppDbContext context) : IBaseRepository<TEntity> where TEntity : class
{
    protected readonly AppDbContext Context = context;

    public async Task AddAsync(TEntity entity, CancellationToken cancellationToken = default) =>
        await Context.Set<TEntity>().AddAsync(entity, cancellationToken);

    public async Task<TEntity?> FindByIdAsync(int id, CancellationToken cancellationToken = default) =>
        await Context.Set<TEntity>().FindAsync([id], cancellationToken);

    public async Task<IEnumerable<TEntity>> ListAsync(CancellationToken cancellationToken = default) =>
        await Context.Set<TEntity>().ToListAsync(cancellationToken);

    public void Update(TEntity entity) => Context.Set<TEntity>().Update(entity);

    public void Remove(TEntity entity) => Context.Set<TEntity>().Remove(entity);
}
