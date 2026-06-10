namespace King.Nexa.Platform.Invoicing.Domain.Model;

public enum InvoicingError
{
    None,
    InvoiceNotFound,
    PaymentNotFound,
    InvoiceCreationFailed,
    InvoiceUpdateFailed,
    InvoiceCancellationFailed,
    PaymentRegistrationFailed,
    InvalidInvoiceData,
    InvalidPaymentData,
    OperationCancelled,
    DatabaseError,
    InternalServerError
}
