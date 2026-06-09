using King.Nexa.Platform.CatalogManagement.Application.CommandServices;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;
using King.Nexa.Platform.CatalogManagement.Domain.Repositories;
using King.Nexa.Platform.Shared.Domain.Repositories;

namespace King.Nexa.Platform.CatalogManagement.Application.Internal.CommandServices;

public class CategoryCommandService(ICategoryRepository categoryRepository, IUnitOfWork unitOfWork) : ICategoryCommandService
{
    public async Task<Category> CreateAsync(CreateCategoryCommand command, CancellationToken cancellationToken = default)
    {
        var category = new Category(command);
        await categoryRepository.AddAsync(category, cancellationToken);
        await unitOfWork.CompleteAsync(cancellationToken);
        return category;
    }

    public async Task<Category?> UpdateAsync(UpdateCategoryCommand command, CancellationToken cancellationToken = default)
    {
        var category = await categoryRepository.FindByIdAsync(command.CategoryId, cancellationToken);
        if (category is null) return null;

        category.Update(command);
        categoryRepository.Update(category);
        await unitOfWork.CompleteAsync(cancellationToken);
        return category;
    }

    public async Task<bool> DeleteAsync(DeleteCategoryCommand command, CancellationToken cancellationToken = default)
    {
        var category = await categoryRepository.FindByIdAsync(command.CategoryId, cancellationToken);
        if (category is null) return false;

        category.Deactivate();
        categoryRepository.Update(category);
        await unitOfWork.CompleteAsync(cancellationToken);
        return true;
    }
}
