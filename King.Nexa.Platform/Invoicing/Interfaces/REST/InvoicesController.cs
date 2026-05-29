using King.Nexa.Platform.Invoicing.Domain.Model.Aggregates;
using King.Nexa.Platform.Invoicing.Domain.Repositories;
using King.Nexa.Platform.Invoicing.Interfaces.REST.Resources;
using King.Nexa.Platform.Invoicing.Interfaces.REST.Transform;
using King.Nexa.Platform.Shared.Domain.Repositories;
using Microsoft.AspNetCore.Mvc;

namespace King.Nexa.Platform.Invoicing.Interfaces.REST;

[ApiController]
[Route("api/v1/[controller]")]
public class InvoicesController(IInvoiceRepository invoiceRepository, IUnitOfWork unitOfWork) : ControllerBase
{
    [HttpGet]
    public async Task<IActionResult> GetAllInvoices(CancellationToken cancellationToken)
    {
        var invoices = await invoiceRepository.ListAsync(cancellationToken);
        return Ok(invoices.Select(InvoiceResourceFromEntityAssembler.ToResourceFromEntity));
    }

    [HttpGet("{id:int}")]
    public async Task<IActionResult> GetInvoiceById(int id, CancellationToken cancellationToken)
    {
        var invoice = await invoiceRepository.FindByIdAsync(id, cancellationToken);
        return invoice is null ? NotFound() : Ok(InvoiceResourceFromEntityAssembler.ToResourceFromEntity(invoice));
    }

    [HttpPost]
    public async Task<IActionResult> GenerateInvoice(GenerateInvoiceResource resource, CancellationToken cancellationToken)
    {
        var command = GenerateInvoiceCommandFromResourceAssembler.ToCommandFromResource(resource);
        var invoice = new Invoice(command);
        await invoiceRepository.AddAsync(invoice, cancellationToken);
        await unitOfWork.CompleteAsync(cancellationToken);
        return CreatedAtAction(nameof(GetInvoiceById), new { id = invoice.Id }, InvoiceResourceFromEntityAssembler.ToResourceFromEntity(invoice));
    }

    [HttpPost("{id:int}/paid")]
    public async Task<IActionResult> MarkInvoicePaid(int id, CancellationToken cancellationToken)
    {
        var invoice = await invoiceRepository.FindByIdAsync(id, cancellationToken);
        if (invoice is null) return NotFound();
        invoice.MarkPaid();
        invoiceRepository.Update(invoice);
        await unitOfWork.CompleteAsync(cancellationToken);
        return Ok(InvoiceResourceFromEntityAssembler.ToResourceFromEntity(invoice));
    }
}
