namespace King.Nexa.Platform.Shared.Domain.Model.Events;

public interface IEvent
{
    DateTime OccurredAt { get; }
}
