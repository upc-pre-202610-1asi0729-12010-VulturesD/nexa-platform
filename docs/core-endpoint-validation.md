# Core Endpoint Validation Payloads

Use this guide after the local MySQL database is created, EF migrations are applied, and `SeedData:Enabled` is set to `true` in `appsettings.Development.json` or `appsettings.Local.json`.

## Local API Base URL

```text
https://localhost:7001/api/v1
```

If the local HTTPS certificate is not trusted, run the backend on HTTP and use:

```text
http://localhost:5000/api/v1
```

## Catalog Management

### List catalog items

```http
GET /api/v1/catalog-items
```

Expected seeded fields:

```json
{
  "id": 1,
  "catalogItemId": "CAT-0001",
  "productId": "PROD-0001",
  "itemName": "QUESO GRANA PADANO DOP 150G",
  "brandName": "Agriform",
  "categoryName": "Cheese",
  "description": "Agriform Queso Grana Padano Dop 150G for refrigerated B2B catalog operations.",
  "imageUrl": "/catalog-items/Agriform_QUESO_GRANA_PADANO_DOP_150G.jpeg",
  "unitPriceAmount": 17.3,
  "unitPriceCurrency": "PEN",
  "availableStock": 160,
  "coldChainRequirement": "Refrigerated",
  "isActive": true
}
```

### Create a catalog item

```http
POST /api/v1/catalog-items
Content-Type: application/json
```

```json
{
  "catalogItemId": "CAT-9001",
  "productId": "PROD-9001",
  "itemName": "QUESO TEST 150G",
  "brandName": "Nexa Demo",
  "categoryName": "Cheese",
  "description": "Demo refrigerated catalog item.",
  "imageUrl": "/catalog-items/demo-product.jpeg",
  "unitPriceAmount": 18.5,
  "unitPriceCurrency": "PEN",
  "availableStock": 120,
  "coldChainRequirement": "Refrigerated"
}
```

## Warehouse

### List inventory items

```http
GET /api/v1/inventory-items
```

Expected seeded fields:

```json
{
  "id": 1,
  "productId": "PROD-0001",
  "catalogItemId": "CAT-0001",
  "availableQuantity": 160,
  "reservedQuantity": 0,
  "warehouseLocation": "Cold Room A-01",
  "minimumTemperature": 2,
  "maximumTemperature": 8
}
```

### Find inventory by catalog item

```http
GET /api/v1/inventory-items/by-catalog-item/CAT-0001
```

### Reserve inventory

```http
POST /api/v1/inventory-items/1/reserve
Content-Type: application/json
```

```json
{
  "reservationCode": "RES-LOCAL-0001",
  "units": 4
}
```

## Sales

### List orders

```http
GET /api/v1/orders
```

Expected seeded statuses:

- `Pending`
- `Confirmed`
- `Rejected`
- `Cancelled`

### Create an order

```http
POST /api/v1/orders
Content-Type: application/json
```

```json
{
  "orderNumber": "ORD-LOCAL-0001",
  "customerId": "CUS-0001",
  "items": [
    {
      "productId": "PROD-0001",
      "catalogItemId": "CAT-0001",
      "itemName": "QUESO GRANA PADANO DOP 150G",
      "quantity": 2,
      "unitPriceAmount": 17.3,
      "unitPriceCurrency": "PEN"
    }
  ]
}
```

### Confirm an order

```http
POST /api/v1/orders/1/confirm
Content-Type: application/json
```

```json
{
  "paymentConfirmation": "PAY-LOCAL-0001",
  "inventoryReservation": "RES-LOCAL-0001"
}
```

### Reject an order

```http
POST /api/v1/orders/1/reject
Content-Type: application/json
```

```json
{
  "rejectionReason": "Credit validation pending."
}
```

### Cancel an order

```http
POST /api/v1/orders/1/cancel
```

## Migration Status

The catalog model now includes `ImageUrl`, mapped to `image_url`. The initial EF migration still must be generated in an environment that has the .NET SDK and EF tooling:

```bash
dotnet ef migrations add InitialCreate \
  --project King.Nexa.Platform \
  --startup-project King.Nexa.Platform \
  --output-dir Migrations

dotnet ef database update \
  --project King.Nexa.Platform \
  --startup-project King.Nexa.Platform
```
