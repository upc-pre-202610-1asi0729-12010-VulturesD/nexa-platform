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
public class CategoriesController(ICategoryCommandService categoryCommandService, ICategoryQueryService categoryQueryService) : ControllerBase
{
    [HttpGet]
    public async Task<IActionResult> GetAllCategories(CancellationToken cancellationToken)
    {
        var categories = await categoryQueryService.Handle(new GetAllCategoriesQuery(), cancellationToken);
        return Ok(categories.Select(CategoryResourceFromEntityAssembler.ToResourceFromEntity));
    }

    [HttpGet("{id:int}")]
    public async Task<IActionResult> GetCategoryById(int id, CancellationToken cancellationToken)
    {
        var category = await categoryQueryService.Handle(new GetCategoryByIdQuery(id), cancellationToken);
        return category is null ? NotFound() : Ok(CategoryResourceFromEntityAssembler.ToResourceFromEntity(category));
    }

    [HttpGet("by-name/{name}")]
    public async Task<IActionResult> GetCategoryByName(string name, CancellationToken cancellationToken)
    {
        var category = await categoryQueryService.Handle(new GetCategoryByNameQuery(name), cancellationToken);
        return category is null ? NotFound() : Ok(CategoryResourceFromEntityAssembler.ToResourceFromEntity(category));
    }

    [HttpPost]
    public async Task<IActionResult> CreateCategory(CreateCategoryResource resource, CancellationToken cancellationToken)
    {
        var command = CreateCategoryCommandFromResourceAssembler.ToCommandFromResource(resource);
        var category = await categoryCommandService.CreateAsync(command, cancellationToken);
        return CreatedAtAction(nameof(GetCategoryById), new { id = category.Id }, CategoryResourceFromEntityAssembler.ToResourceFromEntity(category));
    }

    [HttpPut("{id:int}")]
    public async Task<IActionResult> UpdateCategory(int id, UpdateCategoryResource resource, CancellationToken cancellationToken)
    {
        var command = UpdateCategoryCommandFromResourceAssembler.ToCommandFromResource(id, resource);
        var category = await categoryCommandService.UpdateAsync(command, cancellationToken);
        return category is null ? NotFound() : Ok(CategoryResourceFromEntityAssembler.ToResourceFromEntity(category));
    }

    [HttpDelete("{id:int}")]
    public async Task<IActionResult> DeleteCategory(int id, CancellationToken cancellationToken)
    {
        var deleted = await categoryCommandService.DeleteAsync(new DeleteCategoryCommand(id), cancellationToken);
        return deleted ? NoContent() : NotFound();
    }
}
