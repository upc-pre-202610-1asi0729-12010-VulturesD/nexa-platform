using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;

namespace King.Nexa.Platform.CatalogManagement.Application.CommandServices;

public interface ICategoryCommandService
{
    Task<Category> CreateAsync(CreateCategoryCommand command, CancellationToken cancellationToken = default);

    Task<Category?> UpdateAsync(UpdateCategoryCommand command, CancellationToken cancellationToken = default);

    Task<bool> DeleteAsync(DeleteCategoryCommand command, CancellationToken cancellationToken = default);
}
