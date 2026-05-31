using King.Nexa.Platform.Sales.Domain.Model.Aggregates;
using King.Nexa.Platform.Sales.Domain.Model.Queries;

namespace King.Nexa.Platform.Sales.Application.Services;

public interface IOrderQueryService
{
    Task<IEnumerable<Order>> Handle(GetAllOrdersQuery query, CancellationToken cancellationToken = default);

    Task<Order?> Handle(GetOrderByIdQuery query, CancellationToken cancellationToken = default);
}
