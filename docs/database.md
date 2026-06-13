# Nexa Platform Database Design

## MySQL Profile

Default deployment target is MySQL through the `mysql` profile:

```bash
SPRING_PROFILES_ACTIVE=mysql
DB_HOST=localhost
DB_PORT=3306
DB_NAME=nexa_platform_db
DB_USERNAME=nexa_user
DB_PASSWORD=change-me
```

The schema is generated from Spring Data JPA mappings during academic verification. The local and test profiles use H2 in MySQL compatibility mode. Production secrets are not committed; every credential is supplied through environment variables.

## Bounded Context Tables

| Context | Table | Columns | PK | FK | Constraints | Seeded? |
|---|---|---|---|---|---|---|
| IAM | `iam_users` | `id`, `full_name`, `email`, `password_hash`, `enabled`, audit columns | `id` | none | unique `email`, required password hash | yes |
| IAM | `iam_roles` | `id`, `name` | `id` | none | unique role name | yes |
| IAM | `iam_user_roles` | `user_id`, `role_id` | composite | user, role | user-role join | yes |
| Catalog | `catalog_categories` | `id`, `name`, `description` | `id` | none | unique `name` | yes |
| Catalog | `catalog_products` | `id`, `sku`, `name`, `description`, `category_id`, `supplier_name`, `unit_price`, `unit`, `image_url`, `available_stock`, `reserved_stock`, `min_stock`, `min_celsius`, `max_celsius`, `handling_notes`, `active` | `id` | category | unique `sku`, indexed `sku` | yes |
| Warehouse | `warehouse_warehouses` | `id`, `name`, `address`, `temperature_band` | `id` | none | required name and temperature band | yes |
| Warehouse | `warehouse_inventory_items` | `id`, `warehouse_id`, `product_id`, `quantity_available`, `reorder_point` | `id` | warehouse, product | required quantity and reorder point | yes |
| Warehouse | `warehouse_stock_batches` | `id`, `inventory_item_id`, `lot_code`, `expires_on`, `quantity` | `id` | inventory item | lot and expiration tracking | partial |
| Warehouse | `warehouse_inventory_movements` | `id`, `inventory_item_id`, `type`, `quantity`, `reason`, `created_at` | `id` | inventory item | movement type enum | partial |
| Sales | `sales_customers` | `id`, `business_name`, `tax_id`, `contact_email`, `delivery_address` | `id` | none | required tax and contact fields | yes |
| Sales | `sales_orders` | `id`, `customer_id`, `status`, totals and audit fields | `id` | customer | order status enum | partial |
| Sales | `sales_order_items` | `id`, `order_id`, `product_id`, `quantity`, `unit_price` | `id` | order, product | required product and quantity | partial |
| Logistics | `logistics_delivery_routes` | `id`, `name`, `origin`, `destination` | `id` | none | required route fields | yes |
| Logistics | `logistics_shipments` | `id`, `order_id`, `route_id`, `status`, tracking fields | `id` | order, route | shipment status enum | partial |
| Logistics | `logistics_driver_checklists` | `id`, `shipment_id`, checklist fields | `id` | shipment | evidence checklist | partial |
| Invoicing | `invoicing_invoices` | `id`, `order_id`, `status`, totals and dates | `id` | order | invoice status enum | partial |
| Invoicing | `invoicing_invoice_lines` | `id`, `invoice_id`, `description`, `amount` | `id` | invoice | required amount | partial |
| Invoicing | `invoicing_payments` | `id`, `invoice_id`, `amount`, method and status fields | `id` | invoice | payment status | partial |

## Relationships

- Product records belong to catalog categories.
- Inventory records connect warehouse locations with catalog products.
- Sales orders connect B2B customers with product lines.
- Shipments connect sales orders to delivery routes and driver checklist evidence.
- Invoices and payments connect to order fulfillment and buyer portal history.

## Constraints

- Table and column names are kept in snake_case through explicit table names and the shared physical naming strategy.
- Catalog products use a unique `sku`, exposed to clients as both `sku` and `productCode`.
- IAM users require unique email addresses and BCrypt-hashed passwords.
- MySQL profile uses environment variables only; no production password is hardcoded.

## Seed Data

Local and test profiles seed:

- Admin, operator, source commercial, source logistics, warehouse, and source buyer demo users.
- Catalog categories for Cheese, Charcuterie, Butter, and Dessert.
- 50 source-aligned catalog products from `src/main/resources/data/catalog-items.json` with image URLs under `/assets/catalog-items`.
- One cold hub warehouse, inventory stock for the primary catalog item, one B2B customer, and one logistics route.

## Catalog Image URL Strategy

Images are served by the WebApp as static assets. Platform stores only stable relative URLs such as `/assets/catalog-items/Cavour_SALAME_MILANO_MOLDE_2_5KG.jpeg`. Binary files are not stored in MySQL.
