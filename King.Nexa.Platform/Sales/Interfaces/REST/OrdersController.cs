using King.Nexa.Platform.Sales.Application.Services;
using King.Nexa.Platform.Sales.Domain.Model.Commands;
using King.Nexa.Platform.Sales.Domain.Model.Queries;
using King.Nexa.Platform.Sales.Interfaces.REST.Resources;
using King.Nexa.Platform.Sales.Interfaces.REST.Transform;
using Microsoft.AspNetCore.Mvc;

namespace King.Nexa.Platform.Sales.Interfaces.REST;

[ApiController]
[Route("api/v1/[controller]")]
public class OrdersController(IOrderCommandService orderCommandService, IOrderQueryService orderQueryService) : ControllerBase
{
    [HttpGet]
    public async Task<IActionResult> GetAllOrders(CancellationToken cancellationToken)
    {
        var orders = await orderQueryService.Handle(new GetAllOrdersQuery(), cancellationToken);
        return Ok(orders.Select(OrderResourceFromEntityAssembler.ToResourceFromEntity));
    }

    [HttpGet("{id:int}")]
    public async Task<IActionResult> GetOrderById(int id, CancellationToken cancellationToken)
    {
        var order = await orderQueryService.Handle(new GetOrderByIdQuery(id), cancellationToken);
        return order is null ? NotFound() : Ok(OrderResourceFromEntityAssembler.ToResourceFromEntity(order));
    }

    [HttpPost]
    public async Task<IActionResult> CreateOrder(CreateOrderResource resource, CancellationToken cancellationToken)
    {
        var command = CreateOrderCommandFromResourceAssembler.ToCommandFromResource(resource);
        var order = await orderCommandService.CreateAsync(command, cancellationToken);
        return CreatedAtAction(nameof(GetOrderById), new { id = order.Id }, OrderResourceFromEntityAssembler.ToResourceFromEntity(order));
    }

    [HttpPost("{id:int}/confirm")]
    public async Task<IActionResult> ConfirmOrder(int id, CancellationToken cancellationToken)
    {
        var order = await orderCommandService.ConfirmAsync(new ConfirmOrderCommand(id), cancellationToken);
        if (order is null) return NotFound();
        return Ok(OrderResourceFromEntityAssembler.ToResourceFromEntity(order));
    }
}
