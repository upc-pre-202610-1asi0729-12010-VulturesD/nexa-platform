using King.Nexa.Platform.Sales.Application.CommandServices;
using King.Nexa.Platform.Sales.Application.QueryServices;
using King.Nexa.Platform.Sales.Domain.Model.Aggregates;
using King.Nexa.Platform.Sales.Domain.Model.Queries;
using King.Nexa.Platform.Sales.Domain.Repositories;

namespace King.Nexa.Platform.Sales.Application.Internal.QueryServices;

public class OrderQueryService(IOrderRepository orderRepository) : IOrderQueryService
{
    public async Task<IEnumerable<Order>> Handle(GetAllOrdersQuery query, CancellationToken cancellationToken = default) =>
        await orderRepository.ListWithItemsAsync(cancellationToken);

    public async Task<Order?> Handle(GetOrderByIdQuery query, CancellationToken cancellationToken = default) =>
        await orderRepository.FindByIdWithItemsAsync(query.OrderId, cancellationToken);
}
