using King.Nexa.Platform.Warehouse.Application.CommandServices;
using King.Nexa.Platform.Warehouse.Application.QueryServices;
using King.Nexa.Platform.Warehouse.Domain.Model.Commands;
using King.Nexa.Platform.Warehouse.Domain.Model.Queries;
using King.Nexa.Platform.Warehouse.Interfaces.Rest.Resources;
using King.Nexa.Platform.Warehouse.Interfaces.Rest.Transform;
using Microsoft.AspNetCore.Mvc;

namespace King.Nexa.Platform.Warehouse.Interfaces.Rest;

[ApiController]
[Route("api/v1/[controller]")]
public class WarehousesController(IWarehouseCommandService warehouseCommandService, IWarehouseQueryService warehouseQueryService) : ControllerBase
{
    [HttpGet]
    public async Task<IActionResult> GetAllWarehouses(CancellationToken cancellationToken)
    {
        var warehouses = await warehouseQueryService.Handle(new GetAllWarehousesQuery(), cancellationToken);
        return Ok(warehouses.Select(WarehouseResourceFromEntityAssembler.ToResourceFromEntity));
    }

    [HttpGet("{id:int}")]
    public async Task<IActionResult> GetWarehouseById(int id, CancellationToken cancellationToken)
    {
        var warehouse = await warehouseQueryService.Handle(new GetWarehouseByIdQuery(id), cancellationToken);
        return warehouse is null ? NotFound() : Ok(WarehouseResourceFromEntityAssembler.ToResourceFromEntity(warehouse));
    }

    [HttpGet("by-location/{location}")]
    public async Task<IActionResult> GetWarehouseByLocation(string location, CancellationToken cancellationToken)
    {
        var warehouse = await warehouseQueryService.Handle(new GetWarehouseByLocationQuery(location), cancellationToken);
        return warehouse is null ? NotFound() : Ok(WarehouseResourceFromEntityAssembler.ToResourceFromEntity(warehouse));
    }

    [HttpPost]
    public async Task<IActionResult> CreateWarehouse(CreateWarehouseResource resource, CancellationToken cancellationToken)
    {
        var command = CreateWarehouseCommandFromResourceAssembler.ToCommandFromResource(resource);
        var warehouse = await warehouseCommandService.CreateAsync(command, cancellationToken);
        return CreatedAtAction(nameof(GetWarehouseById), new { id = warehouse.Id }, WarehouseResourceFromEntityAssembler.ToResourceFromEntity(warehouse));
    }

    [HttpPut("{id:int}")]
    public async Task<IActionResult> UpdateWarehouse(int id, UpdateWarehouseResource resource, CancellationToken cancellationToken)
    {
        var command = UpdateWarehouseCommandFromResourceAssembler.ToCommandFromResource(id, resource);
        var warehouse = await warehouseCommandService.UpdateAsync(command, cancellationToken);
        return warehouse is null ? NotFound() : Ok(WarehouseResourceFromEntityAssembler.ToResourceFromEntity(warehouse));
    }

    [HttpDelete("{id:int}")]
    public async Task<IActionResult> DeleteWarehouse(int id, CancellationToken cancellationToken)
    {
        var deleted = await warehouseCommandService.DeleteAsync(new DeleteWarehouseCommand(id), cancellationToken);
        return deleted ? NoContent() : NotFound();
    }
}
