# Database Seed Data

Seed data is active only for `local` and `test` profiles through `DevDataInitializer`.

## IAM

| Email | Role | Purpose |
|---|---|---|
| `admin@nexa.local` | `ROLE_ADMIN` | Admin/demo access |
| `operator@nexa.local` | `ROLE_OPERATOR` | Logistics and warehouse demo access |
| `buyer@nexa.local` | `ROLE_BUYER` | Segment 3 buyer portal demo access |
| `sales@nexa.com` | `ROLE_SALES` | Apps Web commercial quick profile: Valeria Sanchez |
| `logistics@nexa.com` | `ROLE_LOGISTICS` | Apps Web logistics quick profile: Roberto Garcia |
| `warehouse@nexa.com` | `ROLE_WAREHOUSE` | Warehouse/operator profile for S2 coverage |
| `buyer.demo@nexa.com` | `ROLE_BUYER` | Apps Web buyer portal quick profile: Elena Litano, client `CLI-001` |

## Catalog

- Source file: `src/main/resources/data/catalog-items.json`.
- Count: 50 source-aligned products.
- Categories: Cheese, Charcuterie, Butter, Dessert.
- First product: `PROD-0001` / `QUESO GRANA PADANO DOP 150G`.
- Last product: `PROD-0050` / `QUESO MASCARPONE UHT 500G`.
- Image URL strategy: relative WebApp asset path under `/assets/catalog-items`.

## Operations

- Warehouse: Lima Cold Hub.
- Customer: Distribuidora Norte SAC.
- Route: Lima Norte cold route.
- Inventory: stock for the primary catalog item so WebApp inventory cards have platform-backed data.
