using King.Nexa.Platform.Sales.Domain.Model.Aggregates;
using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;
using King.Nexa.Platform.Sales.Domain.Repositories;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EFC.Configuration;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EFC.Repositories;
using Microsoft.EntityFrameworkCore;

namespace King.Nexa.Platform.Sales.Infrastructure.Persistence.EFC.Repositories;

public class OrderRepository(AppDbContext context) : BaseRepository<Order>(context), IOrderRepository
{
    public async Task<Order?> FindByOrderNumberAsync(OrderNumber orderNumber, CancellationToken cancellationToken = default) =>
        await Context.Orders.FirstOrDefaultAsync(order => order.OrderNumber == orderNumber, cancellationToken);
}
