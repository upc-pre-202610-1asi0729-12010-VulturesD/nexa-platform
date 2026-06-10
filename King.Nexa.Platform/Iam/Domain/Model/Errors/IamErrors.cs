using King.Nexa.Platform.Shared.Domain.Model;

namespace King.Nexa.Platform.Iam.Domain.Model.Errors;

public static class IamErrors
{
    public static readonly Error UserNotFound =
        new("Iam.UserNotFound", "The specified user was not found.");

    public static readonly Error UserAlreadyExists =
        new("Iam.UserAlreadyExists", "A user with the specified credentials already exists.");

    public static readonly Error InvalidCredentials =
        new("Iam.InvalidCredentials", "The supplied credentials are invalid.");

    public static readonly Error InvalidToken =
        new("Iam.InvalidToken", "The supplied authentication token is invalid.");

    public static readonly Error Unauthorized =
        new("Iam.Unauthorized", "Authentication is required to access this resource.");

    public static readonly Error Forbidden =
        new("Iam.Forbidden", "The authenticated user is not allowed to perform this action.");

    public static readonly Error RoleNotFound =
        new("Iam.RoleNotFound", "The specified role was not found.");

    public static readonly Error OperationCancelled =
        new("Iam.OperationCancelled", "The identity operation was cancelled.");

    public static readonly Error DatabaseError =
        new("Iam.DatabaseError", "A persistence error occurred while processing identity data.");

    public static readonly Error InternalServerError =
        new("Iam.InternalServerError", "An internal server error occurred while processing the identity request.");
}
