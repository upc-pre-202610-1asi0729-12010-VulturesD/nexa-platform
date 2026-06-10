using King.Nexa.Platform.Invoicing.Application.CommandServices;
using King.Nexa.Platform.Invoicing.Application.QueryServices;
using King.Nexa.Platform.Invoicing.Domain.Model.Commands;
using King.Nexa.Platform.Invoicing.Domain.Model.Queries;
using King.Nexa.Platform.Invoicing.Domain.Model.ValueObjects;
using King.Nexa.Platform.Invoicing.Interfaces.Rest.Resources;
using King.Nexa.Platform.Invoicing.Interfaces.Rest.Transform;
using Microsoft.AspNetCore.Mvc;

namespace King.Nexa.Platform.Invoicing.Interfaces.Rest;

[ApiController]
[Route("api/v1/[controller]")]
public class InvoicesController(IInvoiceCommandService invoiceCommandService, IInvoiceQueryService invoiceQueryService) : ControllerBase
{
    /// <summary>
    /// Gets all generated invoices.
    /// </summary>
    [HttpGet]
    public async Task<IActionResult> GetAllInvoices(CancellationToken cancellationToken)
    {
        var invoices = await invoiceQueryService.Handle(new GetAllInvoicesQuery(), cancellationToken);
        return Ok(invoices.Select(InvoiceResourceFromEntityAssembler.ToResourceFromEntity));
    }

    /// <summary>
    /// Gets one invoice by its numeric identifier.
    /// </summary>
    [HttpGet("{id:int}")]
    public async Task<IActionResult> GetInvoiceById(int id, CancellationToken cancellationToken)
    {
        var invoice = await invoiceQueryService.Handle(new GetInvoiceByIdQuery(id), cancellationToken);
        return invoice is null ? NotFound() : Ok(InvoiceResourceFromEntityAssembler.ToResourceFromEntity(invoice));
    }

    /// <summary>
    /// Gets invoices by order identifier.
    /// </summary>
    [HttpGet("by-order/{orderId:int}")]
    public async Task<IActionResult> GetInvoicesByOrderId(int orderId, CancellationToken cancellationToken)
    {
        var invoices = await invoiceQueryService.Handle(new GetInvoicesByOrderIdQuery(orderId), cancellationToken);
        return Ok(invoices.Select(InvoiceResourceFromEntityAssembler.ToResourceFromEntity));
    }

    /// <summary>
    /// Gets invoices by payment status.
    /// </summary>
    [HttpGet("by-status/{paymentStatus}")]
    public async Task<IActionResult> GetInvoicesByPaymentStatus(string paymentStatus, CancellationToken cancellationToken)
    {
        if (!Enum.TryParse<PaymentStatus>(paymentStatus, true, out var status))
            return BadRequest(new { message = "Invalid payment status." });

        var invoices = await invoiceQueryService.Handle(new GetInvoicesByPaymentStatusQuery(status), cancellationToken);
        return Ok(invoices.Select(InvoiceResourceFromEntityAssembler.ToResourceFromEntity));
    }

    /// <summary>
    /// Generates an invoice for a completed order.
    /// </summary>
    [HttpPost]
    public async Task<IActionResult> GenerateInvoice(GenerateInvoiceResource resource, CancellationToken cancellationToken)
    {
        var command = GenerateInvoiceCommandFromResourceAssembler.ToCommandFromResource(resource);
        var invoice = await invoiceCommandService.GenerateAsync(command, cancellationToken);
        return CreatedAtAction(nameof(GetInvoiceById), new { id = invoice.Id }, InvoiceResourceFromEntityAssembler.ToResourceFromEntity(invoice));
    }

    /// <summary>
    /// Updates an unpaid invoice.
    /// </summary>
    [HttpPut("{id:int}")]
    public async Task<IActionResult> UpdateInvoice(int id, UpdateInvoiceResource resource, CancellationToken cancellationToken)
    {
        var command = UpdateInvoiceCommandFromResourceAssembler.ToCommandFromResource(id, resource);
        var invoice = await invoiceCommandService.UpdateAsync(command, cancellationToken);
        return invoice is null ? NotFound() : Ok(InvoiceResourceFromEntityAssembler.ToResourceFromEntity(invoice));
    }

    /// <summary>
    /// Marks an invoice as paid.
    /// </summary>
    [HttpPost("{id:int}/paid")]
    public async Task<IActionResult> MarkInvoicePaid(int id, CancellationToken cancellationToken)
    {
        var invoice = await invoiceCommandService.MarkPaidAsync(new MarkInvoicePaidCommand(id), cancellationToken);
        if (invoice is null) return NotFound();
        return Ok(InvoiceResourceFromEntityAssembler.ToResourceFromEntity(invoice));
    }

    /// <summary>
    /// Cancels an unpaid invoice.
    /// </summary>
    [HttpDelete("{id:int}")]
    public async Task<IActionResult> CancelInvoice(int id, CancellationToken cancellationToken)
    {
        var cancelled = await invoiceCommandService.CancelAsync(new CancelInvoiceCommand(id), cancellationToken);
        return cancelled ? NoContent() : NotFound();
    }
}
