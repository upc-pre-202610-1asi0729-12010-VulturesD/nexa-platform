using King.Nexa.Platform.Iam.Domain.Model.Aggregates;
using Microsoft.EntityFrameworkCore;

namespace King.Nexa.Platform.Iam.Infrastructure.Persistence.EntityFrameworkCore.Configuration.Extensions;

public static class ModelBuilderExtensions
{
    public static void ApplyIamConfiguration(this ModelBuilder builder)
    {
        var user = builder.Entity<User>();

        user.ToTable("users");
        user.HasKey(entity => entity.Id);
        user.Property(entity => entity.Username).HasColumnName("username").HasMaxLength(80).IsRequired();
        user.Property(entity => entity.Email).HasColumnName("email").HasMaxLength(160).IsRequired();
        user.Property(entity => entity.PasswordHash).HasColumnName("password_hash").HasMaxLength(240).IsRequired();
        user.Property(entity => entity.Role).HasColumnName("role").HasMaxLength(40).IsRequired();
        user.HasIndex(entity => entity.Username).IsUnique();
        user.HasIndex(entity => entity.Email).IsUnique();
    }
}
