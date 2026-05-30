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

- add validation and ProblemDetails error responses;
- create EF Core migrations once database decisions are confirmed;
- add integration tests for REST endpoints;
- define authentication and authorization boundaries;
- connect frontend applications only after stable API contracts exist.

## Deployment note

No production deployment is claimed for v0.1.0. Deployment settings, hosting, secrets management, and operational monitoring belong to later platform milestones.
