# Seed to WebApp Readiness

| WebApp Area | Backend Table(s) | Backend Endpoint(s) | Seed Status | Notes |
|---|---|---|---|---|
| Authentication | `users` | `/api/v1/authentication/sign-in`, `/api/v1/authentication/sign-up` | Ready with existing local users | User insert skipped in AV2 SQL to avoid bypassing password hashing. |
| Catalog items | `catalog_items` | `/api/v1/catalog-items` | Ready | Existing plus AV2 cold-chain catalog rows. |
| Categories | `categories` | `/api/v1/categories` | Ready | Includes AV2 categories and current catalog category names. |
| Brands | `brands` | `/api/v1/brands` | Ready | Includes AV2 brands and current catalog brand names. |
| Inventory items | `inventory_items` | `/api/v1/inventory-items` | Ready | Existing plus AV2 inventory rows across Peruvian locations. |
| Warehouses | `warehouses` | `/api/v1/warehouses` | Ready | Peruvian cold-chain warehouse locations seeded. |
| Orders | `orders`, `order_items` | `/api/v1/orders` | Ready | Existing plus AV2 B2B orders and item detail. |
| Shipments | `shipments` | `/api/v1/shipments` | Ready | Scheduled and delivered shipment states seeded. |
| Invoices | `invoices` | `/api/v1/invoices` | Ready | Pending and paid invoice states seeded. |
| Payments | `payments` | `/api/v1/payments` | Ready | Pending and paid payment states seeded. |

## Pending WebApp Fake API Areas

These areas must stay pending unless matching backend endpoints/tables are added later:

- promotions
- analytics
- temperature logs
- communications
- credit requests
- portal upload tasks
- delivery events beyond current shipment status
- stock movement history beyond current inventory records

Do not seed non-existing backend tables for these areas.
