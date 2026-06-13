# API Contract Matrix

Audit date: 2026-06-12

| WebApp Feature | Expected Endpoint | Platform Endpoint | Method | Request | Response | Status | Action |
|---|---|---|---|---|---|---|---|
| Login | `/api/v1/auth/login` | `/api/v1/auth/login` | POST | `email`, `password` | token, token type, user profile metadata | Aligned | Seeded source quick-profile credentials and segment metadata. |
| Current user | `/api/v1/users/me` | `/api/v1/users/me` | GET | Bearer token | user id, full name, email, roles, profile, scope, segment, workspace, clientId | Aligned | Verified through auth integration test. |
| Catalog items | `/api/v1/catalog-items` | `/api/v1/catalog-items` | GET | Bearer token | source-compatible product resources with `itemName`, `unitPriceAmount`, `availableStock`, `imageUrl` | Aligned | Added endpoint alias backed by catalog query service. |
| Product catalog | `/api/v1/products` | `/api/v1/products` | GET | Bearer token | same product resource as catalog items | Aligned | Preserved existing REST resource and source seed order. |
| Product detail | `/api/v1/catalog-items/{id}` or `/api/v1/products/{id}` | both endpoints | GET | Bearer token | product resource | Aligned | Added detail alias for catalog items. |
| Categories | `/api/v1/categories` | `/api/v1/categories` | GET | Bearer token | category resources | Aligned | Categories are seeded from source catalog categories. |
| Customers | `/api/v1/customers` | `/api/v1/customers` | GET | Bearer token | B2B customer resources | Existing | No Prompt 03 code change required. |
| Orders | `/api/v1/orders` | `/api/v1/orders` | GET/POST | Bearer token; order payload for POST | order resources | Existing | WebApp fallback remains available for richer source mock data. |
| Inventory | `/api/v1/inventory`, `/api/v1/inventory/alerts` | same endpoints | GET | Bearer token | inventory and alert resources | Existing | Uses warehouse bounded context. |
| Warehouses | `/api/v1/warehouses` | `/api/v1/warehouses` | GET | Bearer token | warehouse resources | Existing | Seed includes Lima Cold Hub. |
| Shipments/dispatch | `/api/v1/shipments` | `/api/v1/shipments` | GET/POST | Bearer token; shipment payload for POST | shipment resources | Existing | WebApp dispatch adapter still maps richer fallback data where needed. |
| Shipment tracking | `/api/v1/shipments/{id}/tracking` | same endpoint | GET | Bearer token | tracking resource | Existing | Covered by logistics controller surface. |
| Invoices | `/api/v1/invoices` | `/api/v1/invoices` | GET/POST | Bearer token; invoice payload for POST | invoice resources | Existing | Invoicing context remains compact DTO style. |
| Payments | `/api/v1/payments` | `/api/v1/payments` | POST | Bearer token; payment payload | payment resource | Existing | Payment methods remain WebApp fallback data. |
| Health | `/api/v1/health` | `/api/v1/health` | GET | none | health status | Aligned | Runtime curl verified in Prompt 03. |
| OpenAPI | `/v3/api-docs` | `/v3/api-docs` | GET | none | OpenAPI JSON | Aligned | Test verifies `/catalog-items` and `/auth/login` paths. |

## Contract Notes

- Platform keeps `/api/v1/products` for course REST naming and adds `/api/v1/catalog-items` for Apps Web source-compatible WebApp integration.
- Catalog image URLs remain relative strings owned by the WebApp static asset layer.
- Auth profile metadata is derived from seeded IAM roles and supports commercial, logistics, warehouse, and buyer portal flows.
