using King.Nexa.Platform.Shared.Domain.Model.Events;

namespace King.Nexa.Platform.Shared.Application.Internal.EventHandlers;

public interface IEventHandler<in TEvent> where TEvent : IEvent
{
    Task HandleAsync(TEvent domainEvent, CancellationToken cancellationToken = default);
}
