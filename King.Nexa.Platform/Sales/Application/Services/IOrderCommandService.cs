using King.Nexa.Platform.Sales.Domain.Model.Aggregates;
using King.Nexa.Platform.Sales.Domain.Model.Commands;

namespace King.Nexa.Platform.Sales.Application.Services;

public interface IOrderCommandService
{
    Task<Order> CreateAsync(CreateOrderCommand command, CancellationToken cancellationToken = default);

    Task<Order?> ConfirmAsync(ConfirmOrderCommand command, CancellationToken cancellationToken = default);
}
