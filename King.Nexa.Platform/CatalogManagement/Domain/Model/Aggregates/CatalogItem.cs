using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;
using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;
using King.Nexa.Platform.Shared.Domain.Model.Entities;

namespace King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;

/// <summary>
/// Aggregate root for a commercial item published in the Nexa catalog.
/// </summary>
public class CatalogItem : AuditableEntity
{
    protected CatalogItem()
    {
        CatalogItemId = null!;
        ProductId = null!;
        ItemName = null!;
        BrandName = null!;
        CategoryName = null!;
        Description = string.Empty;
        ImageUrl = string.Empty;
        UnitPrice = null!;
        AvailableStock = null!;
    }

    public CatalogItem(CreateCatalogItemCommand command)
    {
        CatalogItemId = command.CatalogItemId;
        ProductId = command.ProductId;
        ItemName = command.ItemName;
        BrandName = command.BrandName;
        CategoryName = command.CategoryName;
        Description = command.Description.Trim();
        ImageUrl = command.ImageUrl.Trim();
        UnitPrice = command.UnitPrice;
        AvailableStock = command.AvailableStock;
        ColdChainRequirement = command.ColdChainRequirement;
        IsActive = true;
    }

    public CatalogItemId CatalogItemId { get; private set; }

    public ProductId ProductId { get; private set; }

    public ItemName ItemName { get; private set; }

    public BrandName BrandName { get; private set; }

    public CategoryName CategoryName { get; private set; }

    public string Description { get; private set; }

    public string ImageUrl { get; private set; }

    public Money UnitPrice { get; private set; }

    public StockQuantity AvailableStock { get; private set; }

    public ColdChainRequirement ColdChainRequirement { get; private set; }

    public bool IsActive { get; private set; }

    public void Update(UpdateCatalogItemCommand command)
    {
        ItemName = command.ItemName;
        BrandName = command.BrandName;
        CategoryName = command.CategoryName;
        Description = command.Description.Trim();
        ImageUrl = command.ImageUrl.Trim();
        UnitPrice = command.UnitPrice;
        AvailableStock = command.AvailableStock;
        ColdChainRequirement = command.ColdChainRequirement;
    }

    public void Deactivate() => IsActive = false;
}
