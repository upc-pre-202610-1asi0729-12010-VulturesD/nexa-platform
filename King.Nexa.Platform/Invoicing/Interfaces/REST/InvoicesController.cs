using King.Nexa.Platform.Invoicing.Application.Services;
using King.Nexa.Platform.Invoicing.Domain.Model.Commands;
using King.Nexa.Platform.Invoicing.Domain.Model.Queries;
using King.Nexa.Platform.Invoicing.Interfaces.REST.Resources;
using King.Nexa.Platform.Invoicing.Interfaces.REST.Transform;
using Microsoft.AspNetCore.Mvc;

namespace King.Nexa.Platform.Invoicing.Interfaces.REST;

[ApiController]
[Route("api/v1/[controller]")]
public class InvoicesController(IInvoiceCommandService invoiceCommandService, IInvoiceQueryService invoiceQueryService) : ControllerBase
{
    [HttpGet]
    public async Task<IActionResult> GetAllInvoices(CancellationToken cancellationToken)
    {
        var invoices = await invoiceQueryService.Handle(new GetAllInvoicesQuery(), cancellationToken);
        return Ok(invoices.Select(InvoiceResourceFromEntityAssembler.ToResourceFromEntity));
    }

    [HttpGet("{id:int}")]
    public async Task<IActionResult> GetInvoiceById(int id, CancellationToken cancellationToken)
    {
        var invoice = await invoiceQueryService.Handle(new GetInvoiceByIdQuery(id), cancellationToken);
        return invoice is null ? NotFound() : Ok(InvoiceResourceFromEntityAssembler.ToResourceFromEntity(invoice));
    }

    [HttpPost]
    public async Task<IActionResult> GenerateInvoice(GenerateInvoiceResource resource, CancellationToken cancellationToken)
    {
        var command = GenerateInvoiceCommandFromResourceAssembler.ToCommandFromResource(resource);
        var invoice = await invoiceCommandService.GenerateAsync(command, cancellationToken);
        return CreatedAtAction(nameof(GetInvoiceById), new { id = invoice.Id }, InvoiceResourceFromEntityAssembler.ToResourceFromEntity(invoice));
    }

    [HttpPost("{id:int}/paid")]
    public async Task<IActionResult> MarkInvoicePaid(int id, CancellationToken cancellationToken)
    {
        var invoice = await invoiceCommandService.MarkPaidAsync(new MarkInvoicePaidCommand(id), cancellationToken);
        if (invoice is null) return NotFound();
        return Ok(InvoiceResourceFromEntityAssembler.ToResourceFromEntity(invoice));
    }
}
