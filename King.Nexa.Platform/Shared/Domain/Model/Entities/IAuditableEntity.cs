namespace King.Nexa.Platform.Shared.Domain.Model.Entities;

public interface IAuditableEntity
{
    DateTime CreatedAt { get; set; }

    DateTime? UpdatedAt { get; set; }
}
