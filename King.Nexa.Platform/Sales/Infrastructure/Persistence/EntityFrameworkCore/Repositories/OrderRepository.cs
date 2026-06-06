using King.Nexa.Platform.Sales.Domain.Model.Aggregates;
using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;
using King.Nexa.Platform.Sales.Domain.Repositories;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Configuration;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Repositories;
using Microsoft.EntityFrameworkCore;

namespace King.Nexa.Platform.Sales.Infrastructure.Persistence.EntityFrameworkCore.Repositories;

public class OrderRepository(AppDbContext context) : BaseRepository<Order>(context), IOrderRepository
{
    public async Task<Order?> FindByOrderNumberAsync(OrderNumber orderNumber, CancellationToken cancellationToken = default) =>
        await Context.Orders.FirstOrDefaultAsync(order => order.OrderNumber == orderNumber, cancellationToken);

    public async Task<Order?> FindByIdWithItemsAsync(int id, CancellationToken cancellationToken = default) =>
        await Context.Orders.Include(order => order.Items).FirstOrDefaultAsync(order => order.Id == id, cancellationToken);

    public async Task<IEnumerable<Order>> ListWithItemsAsync(CancellationToken cancellationToken = default) =>
        await Context.Orders.Include(order => order.Items).ToListAsync(cancellationToken);
}
