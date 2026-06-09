using King.Nexa.Platform.Sales.Domain.Model.Aggregates;
using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;
using King.Nexa.Platform.Shared.Domain.Repositories;

namespace King.Nexa.Platform.Sales.Domain.Repositories;

public interface IOrderRepository : IBaseRepository<Order>
{
    Task<Order?> FindByOrderNumberAsync(OrderNumber orderNumber, CancellationToken cancellationToken = default);

    Task<Order?> FindByIdWithItemsAsync(int id, CancellationToken cancellationToken = default);

    Task<IEnumerable<Order>> ListWithItemsAsync(CancellationToken cancellationToken = default);

    Task<IEnumerable<Order>> ListByCustomerIdAsync(CustomerId customerId, CancellationToken cancellationToken = default);

    Task<IEnumerable<Order>> ListByStatusAsync(OrderStatus status, CancellationToken cancellationToken = default);
}
