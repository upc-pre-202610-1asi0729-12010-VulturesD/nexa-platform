using King.Nexa.Platform.Logistics.Application.CommandServices;
using King.Nexa.Platform.Logistics.Application.QueryServices;
using King.Nexa.Platform.Logistics.Domain.Model.Commands;
using King.Nexa.Platform.Logistics.Domain.Model.Queries;
using King.Nexa.Platform.Logistics.Domain.Model.ValueObjects;
using King.Nexa.Platform.Logistics.Interfaces.Rest.Resources;
using King.Nexa.Platform.Logistics.Interfaces.Rest.Transform;
using Microsoft.AspNetCore.Mvc;

namespace King.Nexa.Platform.Logistics.Interfaces.Rest;

[ApiController]
[Route("api/v1/[controller]")]
public class ShipmentsController(IShipmentCommandService shipmentCommandService, IShipmentQueryService shipmentQueryService) : ControllerBase
{
    /// <summary>
    /// Gets all shipments scheduled in the logistics workflow.
    /// </summary>
    [HttpGet]
    public async Task<IActionResult> GetAllShipments(CancellationToken cancellationToken)
    {
        var shipments = await shipmentQueryService.Handle(new GetAllShipmentsQuery(), cancellationToken);
        return Ok(shipments.Select(ShipmentResourceFromEntityAssembler.ToResourceFromEntity));
    }

    /// <summary>
    /// Gets one shipment by its numeric identifier.
    /// </summary>
    [HttpGet("{id:int}")]
    public async Task<IActionResult> GetShipmentById(int id, CancellationToken cancellationToken)
    {
        var shipment = await shipmentQueryService.Handle(new GetShipmentByIdQuery(id), cancellationToken);
        return shipment is null ? NotFound() : Ok(ShipmentResourceFromEntityAssembler.ToResourceFromEntity(shipment));
    }

    /// <summary>
    /// Gets shipments by order identifier.
    /// </summary>
    [HttpGet("by-order/{orderId:int}")]
    public async Task<IActionResult> GetShipmentsByOrderId(int orderId, CancellationToken cancellationToken)
    {
        var shipments = await shipmentQueryService.Handle(new GetShipmentsByOrderIdQuery(orderId), cancellationToken);
        return Ok(shipments.Select(ShipmentResourceFromEntityAssembler.ToResourceFromEntity));
    }

    /// <summary>
    /// Gets shipments by delivery status.
    /// </summary>
    [HttpGet("by-status/{status}")]
    public async Task<IActionResult> GetShipmentsByStatus(string status, CancellationToken cancellationToken)
    {
        if (!Enum.TryParse<DeliveryStatus>(status, true, out var deliveryStatus))
            return BadRequest(new { message = "Invalid delivery status." });

        var shipments = await shipmentQueryService.Handle(new GetShipmentsByStatusQuery(deliveryStatus), cancellationToken);
        return Ok(shipments.Select(ShipmentResourceFromEntityAssembler.ToResourceFromEntity));
    }

    /// <summary>
    /// Schedules a shipment for an accepted sales order.
    /// </summary>
    [HttpPost]
    public async Task<IActionResult> ScheduleShipment(ScheduleShipmentResource resource, CancellationToken cancellationToken)
    {
        var command = ScheduleShipmentCommandFromResourceAssembler.ToCommandFromResource(resource);
        var shipment = await shipmentCommandService.ScheduleAsync(command, cancellationToken);
        return CreatedAtAction(nameof(GetShipmentById), new { id = shipment.Id }, ShipmentResourceFromEntityAssembler.ToResourceFromEntity(shipment));
    }

    /// <summary>
    /// Reschedules a shipment.
    /// </summary>
    [HttpPut("{id:int}/schedule")]
    public async Task<IActionResult> RescheduleShipment(int id, RescheduleShipmentResource resource, CancellationToken cancellationToken)
    {
        var command = RescheduleShipmentCommandFromResourceAssembler.ToCommandFromResource(id, resource);
        var shipment = await shipmentCommandService.RescheduleAsync(command, cancellationToken);
        return shipment is null ? NotFound() : Ok(ShipmentResourceFromEntityAssembler.ToResourceFromEntity(shipment));
    }

    /// <summary>
    /// Marks a shipment as delivered.
    /// </summary>
    [HttpPost("{id:int}/delivered")]
    public async Task<IActionResult> MarkShipmentDelivered(int id, CancellationToken cancellationToken)
    {
        var shipment = await shipmentCommandService.MarkDeliveredAsync(new MarkShipmentDeliveredCommand(id), cancellationToken);
        if (shipment is null) return NotFound();
        return Ok(ShipmentResourceFromEntityAssembler.ToResourceFromEntity(shipment));
    }

    /// <summary>
    /// Cancels a shipment that has not been delivered.
    /// </summary>
    [HttpDelete("{id:int}")]
    public async Task<IActionResult> CancelShipment(int id, CancellationToken cancellationToken)
    {
        var cancelled = await shipmentCommandService.CancelAsync(new CancelShipmentCommand(id), cancellationToken);
        return cancelled ? NoContent() : NotFound();
    }
}
