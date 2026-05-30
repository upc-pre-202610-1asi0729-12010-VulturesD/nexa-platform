using King.Nexa.Platform.CatalogManagement.Application.Services;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Queries;
using King.Nexa.Platform.CatalogManagement.Interfaces.REST.Resources;
using King.Nexa.Platform.CatalogManagement.Interfaces.REST.Transform;
using Microsoft.AspNetCore.Mvc;

namespace King.Nexa.Platform.CatalogManagement.Interfaces.REST;

[ApiController]
[Route("api/v1/[controller]")]
public class ProductsController(IProductCommandService productCommandService, IProductQueryService productQueryService) : ControllerBase
{
    [HttpGet]
    public async Task<IActionResult> GetAllProducts(CancellationToken cancellationToken)
    {
        var products = await productQueryService.Handle(new GetAllProductsQuery(), cancellationToken);
        return Ok(products.Select(ProductResourceFromEntityAssembler.ToResourceFromEntity));
    }

    [HttpGet("{id:int}")]
    public async Task<IActionResult> GetProductById(int id, CancellationToken cancellationToken)
    {
        var product = await productQueryService.Handle(new GetProductByIdQuery(id), cancellationToken);
        return product is null ? NotFound() : Ok(ProductResourceFromEntityAssembler.ToResourceFromEntity(product));
    }

    [HttpPost]
    public async Task<IActionResult> CreateProduct(CreateProductResource resource, CancellationToken cancellationToken)
    {
        var command = CreateProductCommandFromResourceAssembler.ToCommandFromResource(resource);
        var product = await productCommandService.CreateAsync(command, cancellationToken);
        return CreatedAtAction(nameof(GetProductById), new { id = product.Id }, ProductResourceFromEntityAssembler.ToResourceFromEntity(product));
    }
}
