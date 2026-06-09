# Platform Roadmap

## v0.1.0 Backend Foundation

Current milestone:

- establish ASP.NET Core solution and project structure;
- define shared kernel and persistence foundation;
- scaffold Sales, Logistics, Warehouse, Invoicing, and Catalog Management bounded contexts;
- expose lightweight REST controllers for early API shape review;
- document GitFlow and platform scope.

## v0.1.1 Application Layer Foundation

Patch milestone:

- add command and query service contracts per bounded context;
- add lightweight internal application services for current REST operations;
- register application services through a shared dependency injection extension;
- keep controllers focused on HTTP resources and responses.

## Next milestones

Planned future work:

- add integration-level validation coverage for ProblemDetails responses;
- create EF Core migrations once the local .NET SDK and `dotnet-ef` CLI are available;
- use `docs/database-setup.md` as the local MySQL and migration runbook;
- add integration tests for REST endpoints;
- define authentication and authorization boundaries;
- connect frontend applications only after stable API contracts exist.

## v0.2.0 Local Modular Monolith Foundation

Local architecture update:

- target .NET 8 and EF Core 8;
- use MySQL persistence through Pomelo;
- configure XML documentation and Swagger XML comments;
- formalize CatalogManagement, Sales, Warehouse, Logistics, Invoicing, IAM, and Shared boundaries;
- prioritize `/api/v1/catalog-items`, `/api/v1/orders`, and `/api/v1/inventory-items`;
- keep migrations pending until local `dotnet` tooling is available.

## Deployment note

No production deployment is claimed for v0.1.0. Deployment settings, hosting, secrets management, and operational monitoring belong to later platform milestones.
