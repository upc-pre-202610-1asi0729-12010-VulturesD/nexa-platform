# Local MySQL Workbench Setup

Use this only for local AV2 Platform validation.

## Create Database

Run in MySQL Workbench:

```sql
CREATE DATABASE IF NOT EXISTS nexa_platform_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE nexa_platform_db;
```

## Configure Local Password

Keep real passwords out of tracked files:

```bash
cp King.Nexa.Platform/appsettings.Local.example.json King.Nexa.Platform/appsettings.Local.json
```

Edit `King.Nexa.Platform/appsettings.Local.json` with the local MySQL password. The file is ignored by Git and is loaded after `appsettings.Development.json`.

## Apply Migrations

```bash
dotnet ef database update --project King.Nexa.Platform/King.Nexa.Platform.csproj
```

Migrations also run when the backend starts. The database schema must exist first.

## Run Backend

```bash
dotnet run --project King.Nexa.Platform/King.Nexa.Platform.csproj
```

Swagger:

```text
http://localhost:5068/swagger
```

## Validate Counts

```sql
USE nexa_platform_db;

SHOW TABLES;

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
