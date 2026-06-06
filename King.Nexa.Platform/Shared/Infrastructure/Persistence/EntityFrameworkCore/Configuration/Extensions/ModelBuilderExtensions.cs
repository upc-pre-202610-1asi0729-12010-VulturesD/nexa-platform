using Humanizer;
using Microsoft.EntityFrameworkCore;

namespace King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Configuration.Extensions;

public static class ModelBuilderExtensions
{
    public static void UseSnakeCasePluralNamingConvention(this ModelBuilder builder)
    {
        foreach (var entity in builder.Model.GetEntityTypes())
        {
            if (entity.ClrType is null || entity.IsOwned())
                continue;

            entity.SetTableName(entity.ClrType.Name.Underscore().Pluralize());

            foreach (var property in entity.GetProperties())
                property.SetColumnName(property.Name.Underscore());

            foreach (var key in entity.GetKeys())
                key.SetName(key.GetName()?.Underscore());

            foreach (var foreignKey in entity.GetForeignKeys())
                foreignKey.SetConstraintName(foreignKey.GetConstraintName()?.Underscore());

            foreach (var index in entity.GetIndexes())
                index.SetDatabaseName(index.GetDatabaseName()?.Underscore());
        }
    }
}
