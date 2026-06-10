namespace King.Nexa.Platform.CatalogManagement.Domain.Model;

public enum CatalogManagementError
{
    None,
    CatalogItemNotFound,
    CatalogItemCreationFailed,
    CatalogItemUpdateFailed,
    CatalogItemDeletionFailed,
    CategoryNotFound,
    BrandNotFound,
    DuplicateCatalogItem,
    InvalidCatalogItemData,
    OperationCancelled,
    DatabaseError,
    InternalServerError
}
