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
| `unitPrice` | Unit price | Order and request summaries |
| `unit` | Commercial unit | Quantity labels |
| `minCelsius`, `maxCelsius` | Cold-chain range | Temperature badge |
| `handlingNotes` | Handling instruction | Product detail |
| `imageUrl` | Relative WebApp static asset URL | Product images |
| `status` | `AVAILABLE` or `INACTIVE` | Availability chip |
| `active` | Operational active flag | Filtering |

## Storage Rule

The Platform stores relative image URLs only. MySQL does not store binary images.

## Compatibility Rule

The WebApp fallback records and Platform seed records must keep matching product names, brand/provider fields, cold-chain metadata, and image URL strategy.
