using King.Nexa.Platform.Sales.Domain.Model.Aggregates;
using King.Nexa.Platform.Sales.Domain.Model.Commands;

namespace King.Nexa.Platform.Sales.Application.CommandServices;

public interface IOrderCommandService
{
    Task<Order> CreateAsync(CreateOrderCommand command, CancellationToken cancellationToken = default);

    Task<Order?> ConfirmAsync(ConfirmOrderCommand command, CancellationToken cancellationToken = default);

    Task<Order?> RejectAsync(RejectOrderCommand command, CancellationToken cancellationToken = default);

    Task<Order?> CancelAsync(CancelOrderCommand command, CancellationToken cancellationToken = default);
}
