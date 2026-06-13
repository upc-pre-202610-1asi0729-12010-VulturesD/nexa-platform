# Professor Architecture Gap Report

Audit date: 2026-06-12

## References

- Open Source course: Angular + TypeScript + Angular Material frontend; Spring Boot + Java REST API; Spring Data JPA; validation/error handling/testing/docs/deployment; OpenAPI/Swagger.
- CatchUp reference zip: bounded context structure with `domain`, `application`, `infrastructure`, `interfaces`, REST resources and transform assemblers, command/query services, repository ports/adapters, global exception handling, snake_case JPA naming.

## Destination Platform Snapshot

| Area | Status |
|---|---|
| Stack | Spring Boot 3.3.6, Java 21, Maven wrapper. |
| Database | H2 test/local and MySQL profile config present. |
| Security | Spring Security + JWT classes present. |
| OpenAPI | springdoc OpenAPI dependency and smoke test present. |
| Tests | `./mvnw -q test` passed. |
| Bounded contexts | `iam`, `catalog`, `sales`, `warehouse`, `logistics`, `invoicing`, `shared`. |

## Package Layout

| Context | Domain | Application | Infrastructure | Interfaces | Gap |
|---|---|---|---|---|---|
| IAM | Entities/roles present | DTOs and services present | security/JPA present | REST controllers present | Uses application DTO mapper style; less explicit command/query split than CatchUp. |
| Catalog | Domain model present | commands, queries, command/query services present | JPA repositories present | resources/transform/controllers present | Closest to CatchUp style. |
| Sales | Domain model present | DTOs/service/mapper present | JPA repositories present | controllers present | Missing explicit REST resources/transform assemblers and command/query service split. |
| Warehouse | Domain model present | DTOs/service/mapper present | JPA repositories present | controllers present | Missing explicit resources/transform assemblers and repository port/adapters. |
| Logistics | Domain model present | DTOs/service/mapper present | JPA repositories present | controllers present | Compact style; needs explainable resources/assemblers if rubric demands. |
| Invoicing | Domain model present | DTOs/service/mapper present | JPA repositories present | controllers present | Compact style; endpoint surface limited. |
| Shared | Base entity, exceptions, config, OpenAPI, CORS, naming strategy | N/A | JPA/config | health/global support | Global exception handling exists but should be verified against CatchUp `GlobalExceptionHandler` pattern. |

## Controllers

Observed destination REST endpoints:

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `GET /api/v1/users/me`
- `GET /api/v1/health`
- `GET/POST/PUT/DELETE /api/v1/products`
- `GET /api/v1/categories`
- `GET /api/v1/customers`
- `GET/POST/PATCH /api/v1/orders`
- `GET /api/v1/inventory`
- `GET /api/v1/inventory/alerts`
- `POST /api/v1/inventory/movements`
- `GET /api/v1/warehouses`
- `GET/POST /api/v1/shipments`
- `GET /api/v1/shipments/{id}/tracking`
- `POST /api/v1/driver-checklists`
- `GET/POST /api/v1/invoices`
- `POST /api/v1/payments`

## Resources And Assemblers

| Area | Status | Required Action |
|---|---|---|
| Catalog | Has `resources` and `transform` assemblers. | Keep as professor-style reference for other contexts. |
| IAM/Sales/Warehouse/Logistics/Invoicing | Mostly DTO/service/mapper style. | For Prompt 03, add or document resource/assembler pattern only where needed by rubric and WebApp contract. |

## Commands/Queries

| Area | Status |
|---|---|
| Catalog | Explicit commands/queries/commandservices/queryservices exist. |
| Other contexts | Mostly service methods with request/response DTOs. |

## Database Config

- `application-local.yml`, `application-mysql.yml`, `application-test.yml` exist.
- H2 test profile works through test suite.
- MySQL readiness is documented in existing docs and must be smoke-checked in Prompt 03 if local MySQL credentials are available.
- Snake case physical naming strategy exists.

## OpenAPI/Swagger

- springdoc dependency present.
- `SwaggerSmokeTests` exists and test suite passes.
- Prompt 03 should run the app and verify `/v3/api-docs` and Swagger UI over HTTP.

## Security

- JWT service/filter/principal/security config exists.
- Auth flow test exists.
- Prompt 03 should verify seeded roles match WebApp quick profiles: commercial, logistics, buyer.

## Data/Seed Contract

- `DevDataInitializer` seeds core data.
- Catalog resource includes `imageUrl` in codebase.
- Current WebApp audit shows data mismatch is primarily WebApp fallback/catalog count/order; Prompt 03 still needs platform seed/API verification after Prompt 02 settles source-aligned data.

## Gap Severity

| Gap | Severity | Fix Timing |
|---|---|---|
| Full Apps Web supplemental endpoint coverage not present | High | Prompt 03 after WebApp contract is final. |
| Non-catalog contexts use compact DTO/service style | Medium | Prompt 03, only where rubric/value justifies. |
| Runtime Swagger not yet verified in this prompt | Medium | Prompt 03. |
| MySQL runtime profile not verified in this prompt | Medium | Prompt 03. |
| Catalog/product image URL contract needs runtime proof | High | Prompt 03. |

## Verdict

Destination Platform is course-stack aligned and tests pass, but it is not yet fully aligned with Apps Web WebApp data needs or CatchUp architecture style across all contexts. Continue to Prompt 03 only after Prompt 02 clarifies final WebApp contracts.
