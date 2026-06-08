using King.Nexa.Platform.Shared.Domain.Model;

namespace King.Nexa.Platform.Invoicing.Domain.Model.Errors;

public static class InvoicingErrors
{
    public static readonly Error InvoiceNotFound =
        new("Invoicing.InvoiceNotFound", "The specified invoice was not found.");

    public static readonly Error PaymentNotFound =
        new("Invoicing.PaymentNotFound", "The specified payment was not found.");

    public static readonly Error InvoiceCreationFailed =
        new("Invoicing.InvoiceCreationFailed", "An error occurred while creating the invoice.");

    public static readonly Error InvoiceUpdateFailed =
        new("Invoicing.InvoiceUpdateFailed", "An error occurred while updating the invoice.");

    public static readonly Error InvoiceCancellationFailed =
        new("Invoicing.InvoiceCancellationFailed", "An error occurred while cancelling the invoice.");

    public static readonly Error PaymentRegistrationFailed =
        new("Invoicing.PaymentRegistrationFailed", "The payment registration could not be completed.");

    public static readonly Error InvalidInvoiceData =
        new("Invoicing.InvalidInvoiceData", "The supplied invoice data is invalid.");

    public static readonly Error InvalidPaymentData =
        new("Invoicing.InvalidPaymentData", "The supplied payment data is invalid.");

    public static readonly Error OperationCancelled =
        new("Invoicing.OperationCancelled", "The invoicing operation was cancelled.");

    public static readonly Error DatabaseError =
        new("Invoicing.DatabaseError", "A persistence error occurred while processing invoicing data.");

    public static readonly Error InternalServerError =
        new("Invoicing.InternalServerError", "An internal server error occurred while processing the invoicing request.");
}
