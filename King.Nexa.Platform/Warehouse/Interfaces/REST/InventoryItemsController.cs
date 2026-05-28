using King.Nexa.Platform.Shared.Domain.Repositories;
using King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;
using King.Nexa.Platform.Warehouse.Domain.Repositories;
using King.Nexa.Platform.Warehouse.Interfaces.REST.Resources;
using King.Nexa.Platform.Warehouse.Interfaces.REST.Transform;
using Microsoft.AspNetCore.Mvc;

namespace King.Nexa.Platform.Warehouse.Interfaces.REST;

[ApiController]
[Route("api/v1/[controller]")]
public class InventoryItemsController(IInventoryItemRepository inventoryItemRepository, IUnitOfWork unitOfWork) : ControllerBase
{
    [HttpGet]
    public async Task<IActionResult> GetAllInventoryItems(CancellationToken cancellationToken)
    {
        var items = await inventoryItemRepository.ListAsync(cancellationToken);
        return Ok(items.Select(InventoryItemResourceFromEntityAssembler.ToResourceFromEntity));
    }

    [HttpGet("{id:int}")]
    public async Task<IActionResult> GetInventoryItemById(int id, CancellationToken cancellationToken)
    {
        var item = await inventoryItemRepository.FindByIdAsync(id, cancellationToken);
        return item is null ? NotFound() : Ok(InventoryItemResourceFromEntityAssembler.ToResourceFromEntity(item));
    }

    [HttpPost]
    public async Task<IActionResult> SyncInventory(SyncInventoryResource resource, CancellationToken cancellationToken)
    {
        var command = SyncInventoryCommandFromResourceAssembler.ToCommandFromResource(resource);
        var item = new InventoryItem(command);
        await inventoryItemRepository.AddAsync(item, cancellationToken);
        await unitOfWork.CompleteAsync(cancellationToken);
        return CreatedAtAction(nameof(GetInventoryItemById), new { id = item.Id }, InventoryItemResourceFromEntityAssembler.ToResourceFromEntity(item));
    }

    [HttpPost("{id:int}/reserve")]
    public async Task<IActionResult> ReserveInventory(int id, ReserveInventoryResource resource, CancellationToken cancellationToken)
    {
        var item = await inventoryItemRepository.FindByIdAsync(id, cancellationToken);
        if (item is null) return NotFound();
        item.Reserve(resource.Units);
        inventoryItemRepository.Update(item);
        await unitOfWork.CompleteAsync(cancellationToken);
        return Ok(InventoryItemResourceFromEntityAssembler.ToResourceFromEntity(item));
    }
}
