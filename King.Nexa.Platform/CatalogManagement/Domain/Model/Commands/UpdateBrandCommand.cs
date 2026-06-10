using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;

public record UpdateBrandCommand(int BrandId, BrandName Name, string Description);
