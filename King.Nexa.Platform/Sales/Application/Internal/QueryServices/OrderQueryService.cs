using King.Nexa.Platform.Sales.Application.CommandServices;
using King.Nexa.Platform.Sales.Application.QueryServices;
using King.Nexa.Platform.Sales.Domain.Model.Aggregates;
using King.Nexa.Platform.Sales.Domain.Model.Queries;
using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;
using King.Nexa.Platform.Sales.Domain.Repositories;

namespace King.Nexa.Platform.Sales.Application.Internal.QueryServices;

public class OrderQueryService(IOrderRepository orderRepository) : IOrderQueryService
{
    public async Task<IEnumerable<Order>> Handle(GetAllOrdersQuery query, CancellationToken cancellationToken = default) =>
        await orderRepository.ListWithItemsAsync(cancellationToken);

    public async Task<Order?> Handle(GetOrderByIdQuery query, CancellationToken cancellationToken = default) =>
        await orderRepository.FindByIdWithItemsAsync(query.OrderId, cancellationToken);

    public async Task<Order?> Handle(GetOrderByOrderNumberQuery query, CancellationToken cancellationToken = default) =>
        await orderRepository.FindByOrderNumberAsync(new OrderNumber(query.OrderNumber), cancellationToken);

    public async Task<IEnumerable<Order>> Handle(GetOrdersByCustomerIdQuery query, CancellationToken cancellationToken = default) =>
        await orderRepository.ListByCustomerIdAsync(new CustomerId(query.CustomerId), cancellationToken);

    public async Task<IEnumerable<Order>> Handle(GetOrdersByStatusQuery query, CancellationToken cancellationToken = default) =>
        await orderRepository.ListByStatusAsync(query.Status, cancellationToken);
}
