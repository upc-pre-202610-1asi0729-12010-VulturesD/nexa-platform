using King.Nexa.Platform.Invoicing.Domain.Model.Aggregates;
using King.Nexa.Platform.Invoicing.Domain.Model.ValueObjects;
using Microsoft.EntityFrameworkCore;

namespace King.Nexa.Platform.Invoicing.Infrastructure.Persistence.EntityFrameworkCore.Configuration.Extensions;

public static class ModelBuilderExtensions
{
    public static void ApplyInvoicingConfiguration(this ModelBuilder builder)
    {
        var invoice = builder.Entity<Invoice>();

        invoice.ToTable("invoices");
        invoice.HasKey(entity => entity.Id);
        invoice.Property(entity => entity.InvoiceNumber)
            .HasConversion(value => value.Value, value => new InvoiceNumber(value))
            .HasColumnName("invoice_number")
            .HasMaxLength(32)
            .IsRequired();
        invoice.OwnsOne(entity => entity.BillingAmount, money =>
        {
            money.Property(value => value.Amount).HasColumnName("amount").HasPrecision(12, 2).IsRequired();
            money.Property(value => value.Currency).HasColumnName("currency").HasMaxLength(3).IsRequired();
        });
        invoice.Property(entity => entity.PaymentStatus)
            .HasColumnName("payment_status")
            .HasConversion<string>()
            .HasMaxLength(24)
            .IsRequired();
        invoice.HasIndex(entity => entity.InvoiceNumber).IsUnique();
    }
}
