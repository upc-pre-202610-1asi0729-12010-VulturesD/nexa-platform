# Catalog Assets Contract

The Platform exposes catalog media metadata. The WebApp owns static image files.

## Product Resource Fields

| Field | Meaning | WebApp Usage |
|---|---|---|
| `id` | Platform product identifier | Detail and order references |
| `sku` | Product SKU | Operations tables |
| `productCode` | Business product code, currently same as SKU | Buyer and catalog cards |
| `name` | Product display name | All catalog surfaces |
| `description` | Commercial product description | Detail pages and modal copy |
| `category` | Category name | Filters and chips |
| `supplierName` | Provider or brand | Operations detail |
| `brandName` | Buyer-facing brand, currently same as supplier | Catalog brand line |
| `brand` | Source-compatible brand alias | Apps Web catalog adapters |
| `unitPrice` | Unit price | Order and request summaries |
| `price` | Source-compatible price alias | Apps Web catalog adapters |
| `unit` | Commercial unit | Quantity labels |
| `minCelsius`, `maxCelsius` | Cold-chain range | Temperature badge |
| `temperatureRange` | Readable range, for example `2C - 8C` | Catalog badges |
| `handlingNotes` | Handling instruction | Product detail |
| `imageUrl` | Relative WebApp static asset URL | Product images |
| `status` | `AVAILABLE` or `INACTIVE` | Availability chip |
| `availability` | Source-compatible availability alias | Catalog status mapping |
| `stock` | Available stock alias | Catalog stock mapping |
| `catalogItemId`, `productId` | Source-compatible identifiers | Apps Web catalog adapters |
| `itemName` | Source-compatible product name | Apps Web catalog adapters |
| `categoryName` | Source-compatible category name | Apps Web catalog adapters |
| `unitPriceAmount`, `unitPriceCurrency` | Source-compatible price object flattening | Apps Web catalog adapters |
| `availableStock`, `reservedStock`, `minStock` | Source-compatible availability values | Catalog stock badges and filters |
| `coldChainRequirement` | Source-compatible cold-chain text | Temperature mapping |
| `isActive` | Source-compatible active flag | Catalog availability mapping |
| `active` | Operational active flag | Filtering |

## Storage Rule

The Platform stores relative image URLs only. MySQL does not store binary images.

## Compatibility Rule

The WebApp fallback records and Platform seed records must keep matching product names, brand/provider fields, cold-chain metadata, order, and image URL strategy.

## Current Seed Source

Platform local/test seed reads `src/main/resources/data/catalog-items.json`, copied from the Open Source WebApp source-aligned fallback catalog. It contains 50 products and keeps the same first product as Apps Web:

`PROD-0001` - `QUESO GRANA PADANO DOP 150G` - `/assets/catalog-items/Agriform_QUESO_GRANA_PADANO_DOP_150G.jpeg`
