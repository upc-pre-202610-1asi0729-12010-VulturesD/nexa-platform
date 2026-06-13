# Platform/API Alignment Report

## Branch

`feature/platform-endpoint-database-assets-alignment`

## Commits Created

- `fd728e1` - `fix(iam): align authentication response with webapp profiles`
- `7ab9b6a` - `fix(api): align platform endpoints with webapp contracts`
- `88b464f` - `feat(catalog): align platform catalog resources with webapp assets`
- `a1b7f68` - `docs(database): document MySQL schema and local runbook`
- `a798dee` - `test(api): validate webapp contract endpoints`
- WebApp integration adapter: `f833619` - `fix(iam): map platform profile roles in webapp auth adapter`

## Endpoint Contract Matrix

See `docs/api-contract-matrix.md`.

Core aligned endpoints:

- `POST /api/v1/auth/login`
- `GET /api/v1/users/me`
- `GET /api/v1/catalog-items`
- `GET /api/v1/catalog-items/{id}`
- `GET /api/v1/products`
- `GET /api/v1/categories`
- `GET /api/v1/health`
- `GET /v3/api-docs`

## Auth/User Switching Contract

Platform local/test seed now supports:

- Valeria Sanchez - `sales@nexa.com` - commercial profile.
- Roberto Garcia - `logistics@nexa.com` - logistics profile.
- Joaquin Verde - `warehouse@nexa.com` - warehouse profile.
- Elena Litano - `buyer.demo@nexa.com` - buyer portal profile with `clientId=CLI-001`.

Auth responses include token, token type, roles, display name, profile, scope, segment, workspace and client id.

## Catalog Data/Image Contract

Catalog local/test seed reads `src/main/resources/data/catalog-items.json`.

Runtime proof:

- `/api/v1/catalog-items` returns 50 items.
- First item is `PROD-0001` / `QUESO GRANA PADANO DOP 150G`.
- First image URL is `/assets/catalog-items/Agriform_QUESO_GRANA_PADANO_DOP_150G.jpeg`.
- Source-compatible fields include `itemName`, `catalogItemId`, `productId`, `unitPriceAmount`, `availableStock`, `reservedStock`, `minStock`, `coldChainRequirement` and `isActive`.

## Database/MySQL Readiness

- H2 local/test profiles remain configured with MySQL compatibility mode.
- MySQL profile remains env-var based through `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` and `JWT_SECRET`.
- No real secrets were committed.
- Database docs added/updated:
  - `docs/database-design.md`
  - `docs/database-seed-data.md`
  - `docs/mysql-local-runbook.md`
  - `docs/database.md`

## Professor Architecture Alignment

- Catalog keeps professor-style `application/commands`, `application/queries`, command/query services, REST resources and transform assemblers.
- New `/catalog-items` alias reuses the existing catalog query service instead of duplicating business logic.
- IAM profile metadata is mapped in application layer, not in controllers.
- Seed data stays in shared infrastructure bootstrap for local/test profiles only.

## Swagger Result

Pass.

- `curl http://localhost:8080/v3/api-docs`: HTTP 200.
- OpenAPI contains `/api/v1/catalog-items` and `/api/v1/auth/login`.

## Tests Result

Pass.

- `./mvnw clean test`: 11 tests, 0 failures.
- `./mvnw clean package`: build success, jar generated.

## WebApp Integration Smoke

Pass.

Evidence file: `docs/audit/platform-webapp-integration-smoke.json`.

Assertions passed:

- Valeria logs in as commercial through Platform auth.
- Catalog page uses Platform `/catalog-items` and renders 50 products with source first product.
- Roberto logs in as logistics and can access inventory route.
- Elena logs in as buyer portal with `CLI-001`.
- Platform auth and catalog endpoints were hit by the browser.

## Remaining Risks

- Supplemental WebApp source/mock routes such as `alerts`, `activity-log`, `dispatches`, `clients` and `inventory-lots` still rely on WebApp fallback data when their first request does not match a Spring endpoint. This preserves current A1 visual/data behavior instead of returning incompatible 200 responses.
- MySQL runtime was documented and H2/local was verified. A real MySQL instance was not started in this prompt.

## Continue to Prompt 04?

YES.
