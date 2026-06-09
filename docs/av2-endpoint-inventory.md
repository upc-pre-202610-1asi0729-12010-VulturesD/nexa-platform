# Nexa AV2 Backend Endpoint Inventory

Scope: endpoint inventory and AV2 backend expansion tracking. Backend endpoints are implemented in code, pending final build validation with .NET 10 SDK where local SDK support is required.

## Local Seed Data Status

Endpoint count: 73 real backend endpoints.

Local AV2 seed data is available in `docs/database/seed-av2-local.sql`.
It is intended for local frontend integration testing only.
It does not replace backend endpoints and must not be used as a fake API.
Frontend must consume backend endpoints, not seed SQL or seed JSON files.

Seed applied locally to `nexa_platform_db`.

Seeded/readiness counts after verification:

| Table | Count |
|---|---:|
| `categories` | 8 |
| `brands` | 10 |
| `warehouses` | 10 |
| `catalog_items` | 58 |
| `inventory_items` | 58 |
| `orders` | 15 |
| `order_items` | 30 |
| `shipments` | 6 |
| `invoices` | 6 |
| `payments` | 5 |

## Frontend Integration Notes

- Frontend features must consume real backend endpoints.
- No json-server or fake API usage should remain in final WebApp features.
- Each frontend feature should map to a real backend endpoint where backend support exists.
- Unsupported frontend-only areas must remain pending until real backend resources exist.
