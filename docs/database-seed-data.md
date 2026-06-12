# Database Seed Data

Seed data is active only for `local` and `test` profiles through `DevDataInitializer`.

## IAM

| Email | Role | Purpose |
|---|---|---|
| `admin@nexa.local` | `ROLE_ADMIN` | Admin/demo access |
| `operator@nexa.local` | `ROLE_OPERATOR` | Logistics and warehouse demo access |
| `buyer@nexa.local` | `ROLE_BUYER` | Segment 3 buyer portal demo access |

## Catalog

| SKU | Product | Brand | Category | Image URL |
|---|---|---|---|---|
| `CAV-SAL-MIL-2K5` | Cavour Salame Milano molde 2.5 kg | Cavour | Charcuterie | `/assets/catalog-items/Cavour_SALAME_MILANO_MOLDE_2_5KG.jpeg` |
| `CAV-SAL-NAP-1K5` | Cavour Salame Napoli molde 1.5 kg | Cavour | Charcuterie | `/assets/catalog-items/Cavour_SALAME_NAPOLI_MOLDE_1_5KG.jpeg` |
| `GES-GOU-NAT-4K5` | Gestam Queso Gouda natural molde 4.5 kg | Gestam | Cheeses | `/assets/catalog-items/Gestam_QUESO_GOUDA_NATURAL_MOLDE_4_5KG.jpeg` |
| `GES-EDA-BOL-1K9` | Gestam Queso Edam bola molde 1.9 kg | Gestam | Cheeses | `/assets/catalog-items/Gestam_QUESO_EDAM_BOLA_MOLDE_1_9KG.jpeg` |
| `GRI-DAN-BLU-100` | Green Island Danish Blue 100 g | Green Island | Cheeses | `/assets/catalog-items/Green_Island_QUESO_DANISH_BLUE_100G.jpeg` |
| `PAY-RAC-SLI-400` | Paysan Breton Raclette slices 400 g | Paysan Breton | Cheeses | `/assets/catalog-items/Paysan_Breton_RACLETTE_SLICES_400G.jpeg` |
| `PAY-BUT-SAL-20U` | Paysan Breton mantequilla con sal 20 x 10 g | Paysan Breton | Butter and Dairy | `/assets/catalog-items/Paysan_Breton_MANTEQUILLA_CON_SAL_20x10G.jpeg` |
| `SAN-MAN-6M-3K` | Sancho Panza Manchego DOP 6 meses molde 3 kg | Sancho Panza | Cheeses | `/assets/catalog-items/Sancho_Panza_QUESO_MANCHEGO_DOP_6_MESES_MOLDE_3KG.jpeg` |

## Operations

- Warehouse: Lima Cold Hub.
- Customer: Distribuidora Norte SAC.
- Route: Lima Norte cold route.
- Inventory: stock for the primary catalog item so WebApp inventory cards have platform-backed data.
