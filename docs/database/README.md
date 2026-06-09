# Nexa AV2 Local Database Seed

Purpose: local/demo data for AV2 frontend integration against real backend endpoints.

This seed is not a fake API, not json-server data, and not frontend source data. WebApp must call backend endpoints, not read this SQL file.

## Files

- `docs/database/seed-av2-local.sql`: idempotent local MySQL seed script.
- `docs/database/seed-to-webapp-readiness.md`: mapping from seeded backend data to WebApp areas.

## Local Warning

Use only with local `nexa_platform_db`.

Do not run this against shared, staging, or production databases.

The script does not use `DROP TABLE`, `TRUNCATE TABLE`, or broad `DELETE FROM`.

## How To Run With MySQL CLI

```bash
mysql -u root -p nexa_platform_db < docs/database/seed-av2-local.sql
```

## How To Run With MySQL Workbench

1. Open MySQL Workbench.
2. Select local `nexa_platform_db`.
3. Open `docs/database/seed-av2-local.sql`.
4. Run full script.
5. Run verification queries at bottom.

## Tables Seeded

- `categories`
- `brands`
- `warehouses`
- `catalog_items`
- `inventory_items`
- `orders`
- `order_items`
- `shipments`
- `invoices`
- `payments`

## Final Local Seed Status

Applied locally on `nexa_platform_db`.

| Table | Count After Seed | Status |
|---|---:|---|
| `users` | 4 | Existing local app seed; no AV2 user insert. |
| `categories` | 8 | Ready |
| `brands` | 10 | Ready |
| `warehouses` | 10 | Ready |
| `catalog_items` | 58 | Ready |
| `inventory_items` | 58 | Ready |
| `orders` | 15 | Ready |
| `order_items` | 30 | Ready |
| `shipments` | 6 | Ready |
| `invoices` | 6 | Ready |
| `payments` | 5 | Ready |

Quality checks passed:

- no duplicate natural keys
- no invalid status values
- no negative inventory quantities
- no orphan order items, shipments, invoices, or payments

## Tables Skipped

| Table | Reason |
|---|---|
| `users` | Existing local app seed already has demo users. Additional user inserts were skipped to avoid bypassing controlled password hashing/sign-up flow. |

## Verification Queries

```sql
USE nexa_platform_db;

SELECT COUNT(*) AS categories_count FROM categories;
SELECT COUNT(*) AS brands_count FROM brands;
SELECT COUNT(*) AS warehouses_count FROM warehouses;
SELECT COUNT(*) AS catalog_items_count FROM catalog_items;
SELECT COUNT(*) AS inventory_items_count FROM inventory_items;
SELECT COUNT(*) AS orders_count FROM orders;
SELECT COUNT(*) AS order_items_count FROM order_items;
SELECT COUNT(*) AS shipments_count FROM shipments;
SELECT COUNT(*) AS invoices_count FROM invoices;
SELECT COUNT(*) AS payments_count FROM payments;
```

## Frontend Integration

Frontend fake API cleanup remains a WebApp task.

WebApp should replace json-server/mock reads with real backend endpoints:

- `/api/v1/categories`
- `/api/v1/brands`
- `/api/v1/warehouses`
- `/api/v1/catalog-items`
- `/api/v1/inventory-items`
- `/api/v1/orders`
- `/api/v1/shipments`
- `/api/v1/invoices`
- `/api/v1/payments`
