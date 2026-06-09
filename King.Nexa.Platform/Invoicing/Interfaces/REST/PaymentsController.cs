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
public class PaymentsController(IPaymentCommandService paymentCommandService, IPaymentQueryService paymentQueryService) : ControllerBase
{
    [HttpGet]
    public async Task<IActionResult> GetAllPayments(CancellationToken cancellationToken)
    {
        var payments = await paymentQueryService.Handle(new GetAllPaymentsQuery(), cancellationToken);
        return Ok(payments.Select(PaymentResourceFromEntityAssembler.ToResourceFromEntity));
    }

    [HttpGet("{id:int}")]
    public async Task<IActionResult> GetPaymentById(int id, CancellationToken cancellationToken)
    {
        var payment = await paymentQueryService.Handle(new GetPaymentByIdQuery(id), cancellationToken);
        return payment is null ? NotFound() : Ok(PaymentResourceFromEntityAssembler.ToResourceFromEntity(payment));
    }

    [HttpGet("by-invoice/{invoiceId:int}")]
    public async Task<IActionResult> GetPaymentsByInvoiceId(int invoiceId, CancellationToken cancellationToken)
    {
        var payments = await paymentQueryService.Handle(new GetPaymentsByInvoiceIdQuery(invoiceId), cancellationToken);
        return Ok(payments.Select(PaymentResourceFromEntityAssembler.ToResourceFromEntity));
    }

    [HttpGet("by-status/{status}")]
    public async Task<IActionResult> GetPaymentsByStatus(string status, CancellationToken cancellationToken)
    {
        if (!Enum.TryParse<PaymentStatus>(status, true, out var paymentStatus))
            return BadRequest(new { message = "Invalid payment status." });

        var payments = await paymentQueryService.Handle(new GetPaymentsByStatusQuery(paymentStatus), cancellationToken);
        return Ok(payments.Select(PaymentResourceFromEntityAssembler.ToResourceFromEntity));
    }

    [HttpPost]
    public async Task<IActionResult> RegisterPayment(RegisterPaymentResource resource, CancellationToken cancellationToken)
    {
        var command = RegisterPaymentCommandFromResourceAssembler.ToCommandFromResource(resource);
        var payment = await paymentCommandService.CreateAsync(command, cancellationToken);
        return CreatedAtAction(nameof(GetPaymentById), new { id = payment.Id }, PaymentResourceFromEntityAssembler.ToResourceFromEntity(payment));
    }

    [HttpPut("{id:int}")]
    public async Task<IActionResult> UpdatePayment(int id, UpdatePaymentResource resource, CancellationToken cancellationToken)
    {
        var command = UpdatePaymentCommandFromResourceAssembler.ToCommandFromResource(id, resource);
        var payment = await paymentCommandService.UpdateAsync(command, cancellationToken);
        return payment is null ? NotFound() : Ok(PaymentResourceFromEntityAssembler.ToResourceFromEntity(payment));
    }

    [HttpDelete("{id:int}")]
    public async Task<IActionResult> DeletePayment(int id, CancellationToken cancellationToken)
    {
        var deleted = await paymentCommandService.DeleteAsync(new DeletePaymentCommand(id), cancellationToken);
        return deleted ? NoContent() : NotFound();
    }
}
