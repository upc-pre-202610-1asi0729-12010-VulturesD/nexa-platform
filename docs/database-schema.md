# Database Schema

Nexa Platform persists bounded-context data through Spring Data JPA mappings.

| Context | Tables | Responsibility |
| --- | --- | --- |
| IAM | `iam_users`, `iam_roles`, `iam_user_roles` | Identity and access control |
| Catalog | `catalog_categories`, `catalog_products` | Product catalog and cold-chain metadata |
| Warehouse | `warehouse_warehouses`, `warehouse_inventory_items`, `warehouse_stock_batches`, `warehouse_inventory_movements` | Stock state, lots, and movement history |
| Sales | `sales_customers`, `sales_orders`, `sales_order_items` | Customer and order lifecycle |
| Logistics | `logistics_delivery_routes`, `logistics_shipments`, `logistics_driver_checklists` | Dispatch planning, tracking, and proof flow |
| Invoicing | `invoicing_invoices`, `invoicing_invoice_lines`, `invoicing_payments` | Billing and payment tracking |
| Promotions | `catalog_promotions` | Commercial promotions |

## Notes

- PostgreSQL is the deployment database.
- H2 is used only for automated verification.
- No production credentials are committed.
