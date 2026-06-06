using King.Nexa.Platform.CatalogManagement.Application.CommandServices;
using King.Nexa.Platform.CatalogManagement.Application.QueryServices;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Queries;
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
    /// Creates a catalog item for the Nexa product catalog.
    /// </summary>
    [HttpPost]
    public async Task<IActionResult> CreateCatalogItem(CreateCatalogItemResource resource, CancellationToken cancellationToken)
    {
        var command = CreateCatalogItemCommandFromResourceAssembler.ToCommandFromResource(resource);
        var catalogItem = await catalogItemCommandService.CreateAsync(command, cancellationToken);
        return CreatedAtAction(nameof(GetCatalogItemById), new { id = catalogItem.Id }, CatalogItemResourceFromEntityAssembler.ToResourceFromEntity(catalogItem));
    }
}
