using King.Nexa.Platform.CatalogManagement.Application.CommandServices;
using King.Nexa.Platform.CatalogManagement.Application.QueryServices;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Queries;
using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;
using King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Resources;
using King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Transform;
using Microsoft.AspNetCore.Mvc;

namespace King.Nexa.Platform.CatalogManagement.Interfaces.Rest;

[ApiController]
[Route("api/v1/[controller]")]
public class CatalogItemsController(ICatalogItemCommandService catalogItemCommandService, ICatalogItemQueryService catalogItemQueryService) : ControllerBase
{
    /// <summary>
    /// Gets all catalog items.
    /// </summary>
    [HttpGet]
    public async Task<IActionResult> GetAllCatalogItems(CancellationToken cancellationToken)
    {
        var catalogItems = await catalogItemQueryService.Handle(new GetAllCatalogItemsQuery(), cancellationToken);
        return Ok(catalogItems.Select(CatalogItemResourceFromEntityAssembler.ToResourceFromEntity));
    }

    /// <summary>
    /// Gets a catalog item by its numeric identifier.
    /// </summary>
    [HttpGet("{id:int}")]
    public async Task<IActionResult> GetCatalogItemById(int id, CancellationToken cancellationToken)
    {
        var catalogItem = await catalogItemQueryService.Handle(new GetCatalogItemByIdQuery(id), cancellationToken);
        return catalogItem is null ? NotFound() : Ok(CatalogItemResourceFromEntityAssembler.ToResourceFromEntity(catalogItem));
    }

    /// <summary>
    /// Gets a catalog item by its stable catalog item identifier.
    /// </summary>
    [HttpGet("by-catalog-item/{catalogItemId}")]
    public async Task<IActionResult> GetCatalogItemByCatalogItemId(string catalogItemId, CancellationToken cancellationToken)
    {
        var catalogItem = await catalogItemQueryService.Handle(new GetCatalogItemByCatalogItemIdQuery(catalogItemId), cancellationToken);
        return catalogItem is null ? NotFound() : Ok(CatalogItemResourceFromEntityAssembler.ToResourceFromEntity(catalogItem));
    }

    /// <summary>
    /// Gets catalog items by category name.
    /// </summary>
    [HttpGet("by-category/{categoryName}")]
    public async Task<IActionResult> GetCatalogItemsByCategoryName(string categoryName, CancellationToken cancellationToken)
    {
        var catalogItems = await catalogItemQueryService.Handle(new GetCatalogItemsByCategoryNameQuery(categoryName), cancellationToken);
        return Ok(catalogItems.Select(CatalogItemResourceFromEntityAssembler.ToResourceFromEntity));
    }

    /// <summary>
    /// Gets catalog items by brand name.
    /// </summary>
    [HttpGet("by-brand/{brandName}")]
    public async Task<IActionResult> GetCatalogItemsByBrandName(string brandName, CancellationToken cancellationToken)
    {
        var catalogItems = await catalogItemQueryService.Handle(new GetCatalogItemsByBrandNameQuery(brandName), cancellationToken);
        return Ok(catalogItems.Select(CatalogItemResourceFromEntityAssembler.ToResourceFromEntity));
    }

    /// <summary>
    /// Gets catalog items by cold-chain requirement.
    /// </summary>
    [HttpGet("cold-chain/{requirement}")]
    public async Task<IActionResult> GetCatalogItemsByColdChainRequirement(string requirement, CancellationToken cancellationToken)
    {
        if (!Enum.TryParse<ColdChainRequirement>(requirement, true, out var coldChainRequirement))
            return BadRequest(new { message = "Invalid cold-chain requirement." });

        var catalogItems = await catalogItemQueryService.Handle(new GetCatalogItemsByColdChainRequirementQuery(coldChainRequirement), cancellationToken);
        return Ok(catalogItems.Select(CatalogItemResourceFromEntityAssembler.ToResourceFromEntity));
    }

    /// <summary>
    /// Creates a catalog item for the Nexa product catalog.
    /// </summary>
    [HttpPost]
    public async Task<IActionResult> CreateCatalogItem(CreateCatalogItemResource resource, CancellationToken cancellationToken)
    {
        var command = CreateCatalogItemCommandFromResourceAssembler.ToCommandFromResource(resource);
        var catalogItem = await catalogItemCommandService.CreateAsync(command, cancellationToken);
        return CreatedAtAction(nameof(GetCatalogItemById), new { id = catalogItem.Id }, CatalogItemResourceFromEntityAssembler.ToResourceFromEntity(catalogItem));
    }

    /// <summary>
    /// Updates an existing catalog item.
    /// </summary>
    [HttpPut("{id:int}")]
    public async Task<IActionResult> UpdateCatalogItem(int id, UpdateCatalogItemResource resource, CancellationToken cancellationToken)
    {
        var command = UpdateCatalogItemCommandFromResourceAssembler.ToCommandFromResource(id, resource);
        var catalogItem = await catalogItemCommandService.UpdateAsync(command, cancellationToken);
        return catalogItem is null ? NotFound() : Ok(CatalogItemResourceFromEntityAssembler.ToResourceFromEntity(catalogItem));
    }

    /// <summary>
    /// Deactivates an existing catalog item.
    /// </summary>
    [HttpDelete("{id:int}")]
    public async Task<IActionResult> DeleteCatalogItem(int id, CancellationToken cancellationToken)
    {
        var deleted = await catalogItemCommandService.DeleteAsync(new DeleteCatalogItemCommand(id), cancellationToken);
        return deleted ? NoContent() : NotFound();
    }
}
