# Platform Architecture Comparison Against CatchUp Reference

| Layer/Pattern | CatchUp Reference | Nexa Platform Current | Gap | Required Action |
|---|---|---|---|---|
| Domain aggregates | Uses `domain/model/aggregates` for aggregate roots. | Domain models are directly under `domain/model` per bounded context. | Aggregate package and naming are not aligned. | Move root entities into aggregate packages where appropriate without changing API behavior. |
| Value objects | Uses `domain/model/valueobjects`. | Catalog has `ColdChainRequirement`; other contexts use enums/entities in flat model packages. | Value-object package pattern is partial. | Introduce value-object packages for cold-chain, address, status, and money-like concepts where useful. |
| Repositories | Uses domain repository contracts under `domain/model/repositories`. | Repositories are Spring Data interfaces under infrastructure persistence packages. | Domain depends implicitly on infrastructure pattern. | Add domain repository contracts and persistence adapters where needed. |
| Commands | Uses `application/commands`. | Request DTOs are under `application/dtos`; no explicit command objects. | Command layer missing. | Add command records/classes for create/update workflows. |
| Queries | Uses `application/queries`. | List/detail methods are exposed directly through services. | Query layer missing. | Add query objects for list/detail/filter use cases. |
| Command services | Uses `application/commandservices` interfaces. | Internal services combine command and query behavior. | Interface split missing. | Add command service contracts per context. |
| Query services | Uses `application/queryservices` interfaces. | Internal services combine read/write behavior. | Interface split missing. | Add query service contracts per context. |
| Internal services | Uses `application/internal/commandservices` and `application/internal/queryservices`. | Uses `application/internal/*Service` plus mappers. | Current implementation is simpler but not reference-aligned. | Split internal services by command/query responsibility. |
| Persistence entities | Uses `infrastructure/persistence/jpa/entities`. | JPA annotations are on domain models. | Persistence and domain are coupled. | Introduce persistence entity classes or document a deliberate exception if scope is constrained. |
| Persistence repositories | Uses `infrastructure/persistence/jpa/repositories`. | Uses `infrastructure/persistence/jpa/*Repository`. | Package name is close but missing `repositories` subpackage. | Move repository interfaces into `repositories` package during architecture alignment. |
| Persistence mappers | Uses `infrastructure/persistence/jpa/mappers`. | Uses application mappers from domain to DTO. | Persistence mapping layer missing. | Add persistence mappers if persistence entities are separated. |
| REST resources | Uses `interfaces/rest/resources`. | Request/response DTOs are under `application/dtos`. | REST resource package missing. | Move external request/response contracts to `interfaces/rest/resources`. |
| REST transformers/assemblers | Uses `interfaces/rest/transform`. | Mappers are in `application/internal`. | REST transform package missing. | Add resource assemblers/transformers per context. |
| Shared result | Uses `shared/application/result`. | Uses exceptions plus `ApiError`, `MessageResponse`, and `PageResponse`. | Result/error pattern not aligned to professor reference. | Add shared result pattern if required by rubric; keep current exception handler for REST safety. |
| Global exception handling | Uses shared REST support. | `GlobalExceptionHandler` and `ApiError` exist. | Mostly present. | Keep and extend with validation/detail errors. |
| i18n messages | Reference includes shared resource/message support. | No message bundle found in resources. | Missing message properties. | Add `messages.properties` and validation messages if rubric demands i18n evidence. |
| MySQL config | Reference expects relational persistence profile. | `application-mysql.yml` exists with environment variables and MySQL dialect. | Present, but schema naming strategy and seed coverage need improvement. | Add naming strategy and profile documentation. |
| OpenAPI/Swagger | Reference exposes API docs. | `OpenApiConfig` exists and Swagger dependencies are present. | Present. | Keep docs reachable and verify in final validation. |

## Package Observations

- Current bounded contexts: IAM, Catalog Management, Warehouse, Sales, Logistics, Invoicing, Shared.
- Current controllers are in `interfaces/rest`, which matches the high-level reference shape.
- Current DTOs and mappers are placed closer to application services than the professor reference expects.
- Current domain models are also persistence entities; this is the largest architecture alignment gap.
- Shared exception handling, CORS, OpenAPI, local seed, and profiles are already present.

## Required Architecture Order

1. Add commands, queries, command service contracts, and query service contracts.
2. Move REST request/response resources and transformers into `interfaces/rest`.
3. Decide whether to separate persistence entities from domain aggregates; if yes, add mappers/adapters per context.
4. Add shared result/message support only where it improves course evidence without breaking existing controllers.
5. Preserve current endpoint behavior while restructuring packages.
