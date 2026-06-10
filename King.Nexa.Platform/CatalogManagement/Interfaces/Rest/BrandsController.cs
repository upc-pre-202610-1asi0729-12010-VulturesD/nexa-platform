using King.Nexa.Platform.CatalogManagement.Application.CommandServices;
using King.Nexa.Platform.CatalogManagement.Application.QueryServices;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Queries;
using King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Resources;
using King.Nexa.Platform.CatalogManagement.Interfaces.Rest.Transform;
using Microsoft.AspNetCore.Mvc;

namespace King.Nexa.Platform.CatalogManagement.Interfaces.Rest;

[ApiController]
[Route("api/v1/[controller]")]
public class BrandsController(IBrandCommandService brandCommandService, IBrandQueryService brandQueryService) : ControllerBase
{
    [HttpGet]
    public async Task<IActionResult> GetAllBrands(CancellationToken cancellationToken)
    {
        var brands = await brandQueryService.Handle(new GetAllBrandsQuery(), cancellationToken);
        return Ok(brands.Select(BrandResourceFromEntityAssembler.ToResourceFromEntity));
    }

    [HttpGet("{id:int}")]
    public async Task<IActionResult> GetBrandById(int id, CancellationToken cancellationToken)
    {
        var brand = await brandQueryService.Handle(new GetBrandByIdQuery(id), cancellationToken);
        return brand is null ? NotFound() : Ok(BrandResourceFromEntityAssembler.ToResourceFromEntity(brand));
    }

    [HttpGet("by-name/{name}")]
    public async Task<IActionResult> GetBrandByName(string name, CancellationToken cancellationToken)
    {
        var brand = await brandQueryService.Handle(new GetBrandByNameQuery(name), cancellationToken);
        return brand is null ? NotFound() : Ok(BrandResourceFromEntityAssembler.ToResourceFromEntity(brand));
    }

    [HttpPost]
    public async Task<IActionResult> CreateBrand(CreateBrandResource resource, CancellationToken cancellationToken)
    {
        var command = CreateBrandCommandFromResourceAssembler.ToCommandFromResource(resource);
        var brand = await brandCommandService.CreateAsync(command, cancellationToken);
        return CreatedAtAction(nameof(GetBrandById), new { id = brand.Id }, BrandResourceFromEntityAssembler.ToResourceFromEntity(brand));
    }

    [HttpPut("{id:int}")]
    public async Task<IActionResult> UpdateBrand(int id, UpdateBrandResource resource, CancellationToken cancellationToken)
    {
        var command = UpdateBrandCommandFromResourceAssembler.ToCommandFromResource(id, resource);
        var brand = await brandCommandService.UpdateAsync(command, cancellationToken);
        return brand is null ? NotFound() : Ok(BrandResourceFromEntityAssembler.ToResourceFromEntity(brand));
    }

    [HttpDelete("{id:int}")]
    public async Task<IActionResult> DeleteBrand(int id, CancellationToken cancellationToken)
    {
        var deleted = await brandCommandService.DeleteAsync(new DeleteBrandCommand(id), cancellationToken);
        return deleted ? NoContent() : NotFound();
    }
}
