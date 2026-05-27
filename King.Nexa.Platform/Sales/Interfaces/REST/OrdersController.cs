using King.Nexa.Platform.Sales.Domain.Model.Aggregates;
using King.Nexa.Platform.Sales.Domain.Repositories;
using King.Nexa.Platform.Sales.Interfaces.REST.Resources;
using King.Nexa.Platform.Sales.Interfaces.REST.Transform;
using King.Nexa.Platform.Shared.Domain.Repositories;
using Microsoft.AspNetCore.Mvc;

namespace King.Nexa.Platform.Sales.Interfaces.REST;

[ApiController]
[Route("api/v1/[controller]")]
public class OrdersController(IOrderRepository orderRepository, IUnitOfWork unitOfWork) : ControllerBase
{
    [HttpGet]
    public async Task<IActionResult> GetAllOrders(CancellationToken cancellationToken)
    {
        var orders = await orderRepository.ListAsync(cancellationToken);
        return Ok(orders.Select(OrderResourceFromEntityAssembler.ToResourceFromEntity));
    }

    [HttpGet("{id:int}")]
    public async Task<IActionResult> GetOrderById(int id, CancellationToken cancellationToken)
    {
        var order = await orderRepository.FindByIdAsync(id, cancellationToken);
        return order is null ? NotFound() : Ok(OrderResourceFromEntityAssembler.ToResourceFromEntity(order));
    }

    [HttpPost]
    public async Task<IActionResult> CreateOrder(CreateOrderResource resource, CancellationToken cancellationToken)
    {
        var command = CreateOrderCommandFromResourceAssembler.ToCommandFromResource(resource);
        var order = new Order(command);
        await orderRepository.AddAsync(order, cancellationToken);
        await unitOfWork.CompleteAsync(cancellationToken);
        return CreatedAtAction(nameof(GetOrderById), new { id = order.Id }, OrderResourceFromEntityAssembler.ToResourceFromEntity(order));
    }

    [HttpPost("{id:int}/confirm")]
    public async Task<IActionResult> ConfirmOrder(int id, CancellationToken cancellationToken)
    {
        var order = await orderRepository.FindByIdAsync(id, cancellationToken);
        if (order is null) return NotFound();
        order.Confirm();
        orderRepository.Update(order);
        await unitOfWork.CompleteAsync(cancellationToken);
        return Ok(OrderResourceFromEntityAssembler.ToResourceFromEntity(order));
    }
}
