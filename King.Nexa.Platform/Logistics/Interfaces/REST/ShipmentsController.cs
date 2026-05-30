using King.Nexa.Platform.Logistics.Application.Services;
using King.Nexa.Platform.Logistics.Domain.Model.Commands;
using King.Nexa.Platform.Logistics.Domain.Model.Queries;
using King.Nexa.Platform.Logistics.Interfaces.REST.Resources;
using King.Nexa.Platform.Logistics.Interfaces.REST.Transform;
using Microsoft.AspNetCore.Mvc;

namespace King.Nexa.Platform.Logistics.Interfaces.REST;

[ApiController]
[Route("api/v1/[controller]")]
public class ShipmentsController(IShipmentCommandService shipmentCommandService, IShipmentQueryService shipmentQueryService) : ControllerBase
{
    [HttpGet]
    public async Task<IActionResult> GetAllShipments(CancellationToken cancellationToken)
    {
        var shipments = await shipmentQueryService.Handle(new GetAllShipmentsQuery(), cancellationToken);
        return Ok(shipments.Select(ShipmentResourceFromEntityAssembler.ToResourceFromEntity));
    }

    [HttpGet("{id:int}")]
    public async Task<IActionResult> GetShipmentById(int id, CancellationToken cancellationToken)
    {
        var shipment = await shipmentQueryService.Handle(new GetShipmentByIdQuery(id), cancellationToken);
        return shipment is null ? NotFound() : Ok(ShipmentResourceFromEntityAssembler.ToResourceFromEntity(shipment));
    }

    [HttpPost]
    public async Task<IActionResult> ScheduleShipment(ScheduleShipmentResource resource, CancellationToken cancellationToken)
    {
        var command = ScheduleShipmentCommandFromResourceAssembler.ToCommandFromResource(resource);
        var shipment = await shipmentCommandService.ScheduleAsync(command, cancellationToken);
        return CreatedAtAction(nameof(GetShipmentById), new { id = shipment.Id }, ShipmentResourceFromEntityAssembler.ToResourceFromEntity(shipment));
    }

    [HttpPost("{id:int}/delivered")]
    public async Task<IActionResult> MarkShipmentDelivered(int id, CancellationToken cancellationToken)
    {
        var shipment = await shipmentCommandService.MarkDeliveredAsync(new MarkShipmentDeliveredCommand(id), cancellationToken);
        if (shipment is null) return NotFound();
        return Ok(ShipmentResourceFromEntityAssembler.ToResourceFromEntity(shipment));
    }
}
