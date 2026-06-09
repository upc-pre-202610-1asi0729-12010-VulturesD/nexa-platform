using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;

public record CreateCategoryCommand(CategoryName Name, string Description);
