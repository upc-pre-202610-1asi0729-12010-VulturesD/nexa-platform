using King.Nexa.Platform.Sales.Application.CommandServices;
using King.Nexa.Platform.Sales.Application.QueryServices;
using King.Nexa.Platform.Sales.Domain.Model.Commands;
using King.Nexa.Platform.Sales.Domain.Model.Queries;
using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;
using King.Nexa.Platform.Sales.Interfaces.Rest.Resources;
using King.Nexa.Platform.Sales.Interfaces.Rest.Transform;
using Microsoft.AspNetCore.Mvc;

namespace King.Nexa.Platform.Sales.Interfaces.Rest;

[ApiController]
[Route("api/v1/[controller]")]
public class OrdersController(IOrderCommandService orderCommandService, IOrderQueryService orderQueryService) : ControllerBase
{
    /// <summary>
    /// Gets all orders.
    /// </summary>
    [HttpGet]
    public async Task<IActionResult> GetAllOrders(CancellationToken cancellationToken)
    {
        var orders = await orderQueryService.Handle(new GetAllOrdersQuery(), cancellationToken);
        return Ok(orders.Select(OrderResourceFromEntityAssembler.ToResourceFromEntity));
    }

    /// <summary>
    /// Gets an order by its numeric identifier.
    /// </summary>
    [HttpGet("{id:int}")]
    public async Task<IActionResult> GetOrderById(int id, CancellationToken cancellationToken)
    {
        var order = await orderQueryService.Handle(new GetOrderByIdQuery(id), cancellationToken);
        return order is null ? NotFound() : Ok(OrderResourceFromEntityAssembler.ToResourceFromEntity(order));
    }

    /// <summary>
    /// Creates an order with at least one order item.
    /// </summary>
    [HttpPost]
    public async Task<IActionResult> CreateOrder(CreateOrderResource resource, CancellationToken cancellationToken)
    {
        var command = CreateOrderCommandFromResourceAssembler.ToCommandFromResource(resource);
        var order = await orderCommandService.CreateAsync(command, cancellationToken);
        return CreatedAtAction(nameof(GetOrderById), new { id = order.Id }, OrderResourceFromEntityAssembler.ToResourceFromEntity(order));
    }

    [HttpPost("{id:int}/confirm")]
    public async Task<IActionResult> ConfirmOrder(int id, ConfirmOrderResource resource, CancellationToken cancellationToken)
    {
        var order = await orderCommandService.ConfirmAsync(
            new ConfirmOrderCommand(id, new PaymentConfirmation(resource.PaymentConfirmation), new InventoryReservation(resource.InventoryReservation)),
            cancellationToken);
        if (order is null) return NotFound();
        return Ok(OrderResourceFromEntityAssembler.ToResourceFromEntity(order));
    }

    [HttpPost("{id:int}/reject")]
    public async Task<IActionResult> RejectOrder(int id, RejectOrderResource resource, CancellationToken cancellationToken)
    {
        var order = await orderCommandService.RejectAsync(new RejectOrderCommand(id, new RejectionReason(resource.RejectionReason)), cancellationToken);
        if (order is null) return NotFound();
        return Ok(OrderResourceFromEntityAssembler.ToResourceFromEntity(order));
    }

    [HttpPost("{id:int}/cancel")]
    public async Task<IActionResult> CancelOrder(int id, CancellationToken cancellationToken)
    {
        var order = await orderCommandService.CancelAsync(new CancelOrderCommand(id), cancellationToken);
        if (order is null) return NotFound();
        return Ok(OrderResourceFromEntityAssembler.ToResourceFromEntity(order));
    }
}
