using King.Nexa.Platform.Sales.Domain.Model.Aggregates;
using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;
using King.Nexa.Platform.Shared.Domain.Repositories;

namespace King.Nexa.Platform.Sales.Domain.Repositories;

public interface IOrderRepository : IBaseRepository<Order>
{
    Task<Order?> FindByOrderNumberAsync(OrderNumber orderNumber, CancellationToken cancellationToken = default);
}
