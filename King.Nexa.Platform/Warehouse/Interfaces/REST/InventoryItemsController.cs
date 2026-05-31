using King.Nexa.Platform.Warehouse.Application.Services;
using King.Nexa.Platform.Warehouse.Domain.Model.Commands;
using King.Nexa.Platform.Warehouse.Domain.Model.Queries;
using King.Nexa.Platform.Warehouse.Interfaces.REST.Resources;
using King.Nexa.Platform.Warehouse.Interfaces.REST.Transform;
using Microsoft.AspNetCore.Mvc;

namespace King.Nexa.Platform.Warehouse.Interfaces.REST;

[ApiController]
[Route("api/v1/[controller]")]
public class InventoryItemsController(IInventoryItemCommandService inventoryItemCommandService, IInventoryItemQueryService inventoryItemQueryService) : ControllerBase
{
    [HttpGet]
    public async Task<IActionResult> GetAllInventoryItems(CancellationToken cancellationToken)
    {
        var items = await inventoryItemQueryService.Handle(new GetAllInventoryItemsQuery(), cancellationToken);
        return Ok(items.Select(InventoryItemResourceFromEntityAssembler.ToResourceFromEntity));
    }

    [HttpGet("{id:int}")]
    public async Task<IActionResult> GetInventoryItemById(int id, CancellationToken cancellationToken)
    {
        var item = await inventoryItemQueryService.Handle(new GetInventoryItemByIdQuery(id), cancellationToken);
        return item is null ? NotFound() : Ok(InventoryItemResourceFromEntityAssembler.ToResourceFromEntity(item));
    }

    [HttpPost]
    public async Task<IActionResult> SyncInventory(SyncInventoryResource resource, CancellationToken cancellationToken)
    {
        var command = SyncInventoryCommandFromResourceAssembler.ToCommandFromResource(resource);
        var item = await inventoryItemCommandService.SyncAsync(command, cancellationToken);
        return CreatedAtAction(nameof(GetInventoryItemById), new { id = item.Id }, InventoryItemResourceFromEntityAssembler.ToResourceFromEntity(item));
    }

    [HttpPost("{id:int}/reserve")]
    public async Task<IActionResult> ReserveInventory(int id, ReserveInventoryResource resource, CancellationToken cancellationToken)
    {
        var item = await inventoryItemCommandService.ReserveAsync(new ReserveInventoryCommand(id, resource.Units), cancellationToken);
        if (item is null) return NotFound();
        return Ok(InventoryItemResourceFromEntityAssembler.ToResourceFromEntity(item));
    }
}
