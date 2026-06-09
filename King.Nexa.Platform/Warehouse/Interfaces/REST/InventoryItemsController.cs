using King.Nexa.Platform.Warehouse.Application.CommandServices;
using King.Nexa.Platform.Warehouse.Application.QueryServices;
using King.Nexa.Platform.Warehouse.Domain.Model.Commands;
using King.Nexa.Platform.Warehouse.Domain.Model.Queries;
using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;
using King.Nexa.Platform.Warehouse.Interfaces.Rest.Resources;
using King.Nexa.Platform.Warehouse.Interfaces.Rest.Transform;
using Microsoft.AspNetCore.Mvc;

namespace King.Nexa.Platform.Warehouse.Interfaces.Rest;

[ApiController]
[Route("api/v1/[controller]")]
public class InventoryItemsController(IInventoryItemCommandService inventoryItemCommandService, IInventoryItemQueryService inventoryItemQueryService) : ControllerBase
{
    /// <summary>
    /// Gets all inventory items.
    /// </summary>
    [HttpGet]
    public async Task<IActionResult> GetAllInventoryItems(CancellationToken cancellationToken)
    {
        var items = await inventoryItemQueryService.Handle(new GetAllInventoryItemsQuery(), cancellationToken);
        return Ok(items.Select(InventoryItemResourceFromEntityAssembler.ToResourceFromEntity));
    }

    /// <summary>
    /// Gets one inventory item by its numeric identifier.
    /// </summary>
    [HttpGet("{id:int}")]
    public async Task<IActionResult> GetInventoryItemById(int id, CancellationToken cancellationToken)
    {
        var item = await inventoryItemQueryService.Handle(new GetInventoryItemByIdQuery(id), cancellationToken);
        return item is null ? NotFound() : Ok(InventoryItemResourceFromEntityAssembler.ToResourceFromEntity(item));
    }

    /// <summary>
    /// Gets one inventory item by the catalog item identifier shared with sales.
    /// </summary>
    [HttpGet("by-catalog-item/{catalogItemId}")]
    public async Task<IActionResult> GetInventoryItemByCatalogItemId(string catalogItemId, CancellationToken cancellationToken)
    {
        var item = await inventoryItemQueryService.Handle(new GetInventoryItemByCatalogItemIdQuery(catalogItemId), cancellationToken);
        return item is null ? NotFound() : Ok(InventoryItemResourceFromEntityAssembler.ToResourceFromEntity(item));
    }

    /// <summary>
    /// Gets inventory items by warehouse location.
    /// </summary>
    [HttpGet("by-warehouse-location/{warehouseLocation}")]
    public async Task<IActionResult> GetInventoryItemsByWarehouseLocation(string warehouseLocation, CancellationToken cancellationToken)
    {
        var items = await inventoryItemQueryService.Handle(new GetInventoryItemsByWarehouseLocationQuery(warehouseLocation), cancellationToken);
        return Ok(items.Select(InventoryItemResourceFromEntityAssembler.ToResourceFromEntity));
    }

    /// <summary>
    /// Gets inventory items whose available quantity is at or below the threshold.
    /// </summary>
    [HttpGet("low-stock/{threshold:int}")]
    public async Task<IActionResult> GetLowStockInventoryItems(int threshold, CancellationToken cancellationToken)
    {
        var items = await inventoryItemQueryService.Handle(new GetLowStockInventoryItemsQuery(threshold), cancellationToken);
        return Ok(items.Select(InventoryItemResourceFromEntityAssembler.ToResourceFromEntity));
    }

    /// <summary>
    /// Creates an inventory item.
    /// </summary>
    [HttpPost]
    public async Task<IActionResult> CreateInventoryItem(CreateInventoryItemResource resource, CancellationToken cancellationToken)
    {
        var command = CreateInventoryItemCommandFromResourceAssembler.ToCommandFromResource(resource);
        var item = await inventoryItemCommandService.CreateAsync(command, cancellationToken);
        return CreatedAtAction(nameof(GetInventoryItemById), new { id = item.Id }, InventoryItemResourceFromEntityAssembler.ToResourceFromEntity(item));
    }

    /// <summary>
    /// Updates an inventory item.
    /// </summary>
    [HttpPut("{id:int}")]
    public async Task<IActionResult> UpdateInventoryItem(int id, UpdateInventoryItemResource resource, CancellationToken cancellationToken)
    {
        var command = UpdateInventoryItemCommandFromResourceAssembler.ToCommandFromResource(id, resource);
        var item = await inventoryItemCommandService.UpdateAsync(command, cancellationToken);
        return item is null ? NotFound() : Ok(InventoryItemResourceFromEntityAssembler.ToResourceFromEntity(item));
    }

    /// <summary>
    /// Deletes an inventory item.
    /// </summary>
    [HttpDelete("{id:int}")]
    public async Task<IActionResult> DeleteInventoryItem(int id, CancellationToken cancellationToken)
    {
        var deleted = await inventoryItemCommandService.DeleteAsync(new DeleteInventoryItemCommand(id), cancellationToken);
        return deleted ? NoContent() : NotFound();
    }

    /// <summary>
    /// Reserves inventory units for a sales order workflow.
    /// </summary>
    [HttpPost("{id:int}/reserve")]
    public async Task<IActionResult> ReserveInventory(int id, ReserveInventoryResource resource, CancellationToken cancellationToken)
    {
        var item = await inventoryItemCommandService.ReserveAsync(
            new ReserveInventoryCommand(id, new InventoryReservation(resource.ReservationCode, resource.Units)),
            cancellationToken);
        if (item is null) return NotFound();
        return Ok(InventoryItemResourceFromEntityAssembler.ToResourceFromEntity(item));
    }

    /// <summary>
    /// Releases previously reserved inventory units.
    /// </summary>
    [HttpPost("{id:int}/release-reservation")]
    public async Task<IActionResult> ReleaseInventoryReservation(int id, ReserveInventoryResource resource, CancellationToken cancellationToken)
    {
        var item = await inventoryItemCommandService.ReleaseAsync(
            new ReleaseInventoryReservationCommand(id, new InventoryReservation(resource.ReservationCode, resource.Units)),
            cancellationToken);
        if (item is null) return NotFound();
        return Ok(InventoryItemResourceFromEntityAssembler.ToResourceFromEntity(item));
    }
}
