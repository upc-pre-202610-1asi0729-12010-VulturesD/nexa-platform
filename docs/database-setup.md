# Database Setup

Nexa Platform uses one physical MySQL database for all bounded contexts.

## Local Configuration

Keep real passwords out of tracked files. Copy the local example and edit it on your machine:

```bash
cp King.Nexa.Platform/appsettings.Local.example.json King.Nexa.Platform/appsettings.Local.json
```

`appsettings.Local.json` is ignored by Git and is loaded after `appsettings.Development.json`.

## Create the Database

Run this script in MySQL Workbench before applying EF Core migrations:

```sql
CREATE DATABASE IF NOT EXISTS nexa_platform_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE nexa_platform_db;
```

The same script lives in `King.Nexa.Platform/Migrations/mysql-init.sql`.

## Generate and Apply EF Migrations

Run these commands from the repository root on a machine with the .NET SDK and `dotnet-ef` installed:

```bash
dotnet ef migrations add InitialCreate \
  --project King.Nexa.Platform \
  --startup-project King.Nexa.Platform \
  --output-dir Migrations

dotnet ef database update \
  --project King.Nexa.Platform \
  --startup-project King.Nexa.Platform
```

Expected tables after `database update`:

- `catalog_items`
- `orders`
- `order_items`
- `inventory_items`
- `shipments`
- `invoices`
- `users`
- `__ef_migrations_history`

Prefer loading data through the API after migrations, because audit columns are filled by the EF Core interceptor.

## Development Seed Data

The backend includes development seed files under `King.Nexa.Platform/Resources/SeedData/`.
Seed execution is controlled by:

```json
{
  "SeedData": {
    "Enabled": true
  }
}
```

Keep `SeedData:Enabled` disabled outside local development or explicit demo environments.
