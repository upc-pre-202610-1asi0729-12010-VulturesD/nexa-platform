using King.Nexa.Platform.Sales.Application.Services;
using King.Nexa.Platform.Sales.Domain.Model.Aggregates;
using King.Nexa.Platform.Sales.Domain.Model.Commands;
using King.Nexa.Platform.Sales.Domain.Repositories;
using King.Nexa.Platform.Shared.Domain.Repositories;

namespace King.Nexa.Platform.Sales.Application.Internal.CommandServices;

public class OrderCommandService(IOrderRepository orderRepository, IUnitOfWork unitOfWork) : IOrderCommandService
{
    public async Task<Order> CreateAsync(CreateOrderCommand command, CancellationToken cancellationToken = default)
    {
        var order = new Order(command);
        await orderRepository.AddAsync(order, cancellationToken);
        await unitOfWork.CompleteAsync(cancellationToken);
        return order;
    }

    public async Task<Order?> ConfirmAsync(ConfirmOrderCommand command, CancellationToken cancellationToken = default)
    {
        var order = await orderRepository.FindByIdAsync(command.OrderId, cancellationToken);
        if (order is null) return null;

        order.Confirm();
        orderRepository.Update(order);
        await unitOfWork.CompleteAsync(cancellationToken);
        return order;
    }
}
