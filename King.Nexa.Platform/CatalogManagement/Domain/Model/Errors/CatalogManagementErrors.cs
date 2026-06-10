using King.Nexa.Platform.Shared.Domain.Model;

namespace King.Nexa.Platform.CatalogManagement.Domain.Model.Errors;

public static class CatalogManagementErrors
{
    public static readonly Error CatalogItemNotFound =
        new("CatalogManagement.CatalogItemNotFound", "The specified catalog item was not found.");

    public static readonly Error CatalogItemCreationFailed =
        new("CatalogManagement.CatalogItemCreationFailed", "An error occurred while creating the catalog item.");

    public static readonly Error CatalogItemUpdateFailed =
        new("CatalogManagement.CatalogItemUpdateFailed", "An error occurred while updating the catalog item.");

    public static readonly Error CatalogItemDeletionFailed =
        new("CatalogManagement.CatalogItemDeletionFailed", "An error occurred while deleting the catalog item.");

    public static readonly Error CategoryNotFound =
        new("CatalogManagement.CategoryNotFound", "The specified catalog category was not found.");

    public static readonly Error BrandNotFound =
        new("CatalogManagement.BrandNotFound", "The specified catalog brand was not found.");

    public static readonly Error DuplicateCatalogItem =
        new("CatalogManagement.DuplicateCatalogItem", "A catalog item with the specified business identifiers already exists.");

    public static readonly Error InvalidCatalogItemData =
        new("CatalogManagement.InvalidCatalogItemData", "The supplied catalog item data is invalid.");

    public static readonly Error OperationCancelled =
        new("CatalogManagement.OperationCancelled", "The catalog management operation was cancelled.");

    public static readonly Error DatabaseError =
        new("CatalogManagement.DatabaseError", "A persistence error occurred while processing catalog data.");

    public static readonly Error InternalServerError =
        new("CatalogManagement.InternalServerError", "An internal server error occurred while processing the catalog request.");
}
