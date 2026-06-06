using King.Nexa.Platform.Sales.Domain.Model.Commands;
using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;
using King.Nexa.Platform.Shared.Domain.Model.Entities;

namespace King.Nexa.Platform.Sales.Domain.Model.Entities;

/// <summary>
/// Entity representing one catalog item requested in an order.
/// </summary>
public class OrderItem : Entity
{
    protected OrderItem()
    {
        ProductId = null!;
        CatalogItemId = null!;
        ItemName = null!;
        Quantity = null!;
        UnitPrice = null!;
        Subtotal = null!;
    }

    public OrderItem(CreateOrderItemCommand command)
    {
        ProductId = command.ProductId;
        CatalogItemId = command.CatalogItemId;
        ItemName = command.ItemName;
        Quantity = command.Quantity;
        UnitPrice = command.UnitPrice;
        Subtotal = command.UnitPrice.Multiply(command.Quantity.Value);
    }

    public int OrderId { get; private set; }

    public ProductId ProductId { get; private set; }

    public CatalogItemId CatalogItemId { get; private set; }

    public ItemName ItemName { get; private set; }

    public Quantity Quantity { get; private set; }

    public Money UnitPrice { get; private set; }

    public Money Subtotal { get; private set; }
}
