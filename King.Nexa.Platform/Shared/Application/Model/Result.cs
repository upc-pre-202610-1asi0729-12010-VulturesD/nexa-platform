namespace King.Nexa.Platform.Shared.Application.Model;

public abstract record Result<TValue, TError>
{
    public sealed record Success(TValue Value) : Result<TValue, TError>;

    public sealed record Failure(TError Error) : Result<TValue, TError>;

    public bool IsSuccess => this is Success;

    public bool IsFailure => this is Failure;

    public TResult Fold<TResult>(Func<TValue, TResult> onSuccess, Func<TError, TResult> onFailure) =>
        this switch
        {
            Success success => onSuccess(success.Value),
            Failure failure => onFailure(failure.Error),
            _ => throw new InvalidOperationException("Unknown result type")
        };
}
