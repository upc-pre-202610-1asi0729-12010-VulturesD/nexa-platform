namespace King.Nexa.Platform.Iam.Domain.Model;

public enum IamError
{
    None,
    UserNotFound,
    UserAlreadyExists,
    InvalidCredentials,
    InvalidToken,
    Unauthorized,
    Forbidden,
    RoleNotFound,
    OperationCancelled,
    DatabaseError,
    InternalServerError
}
