# Database Seed Data

The local and test profiles create workspace records for validation. Render academic demos enable the same idempotent data with `SPRING_PROFILES_ACTIVE=postgres,seed`.

## Workspace Users

| Email | Role | Purpose |
| --- | --- | --- |
| `admin@nexa.local` | `ROLE_ADMIN` | Platform administration |
| `operator@nexa.local` | `ROLE_OPERATOR` | Operations access |
| `buyer@nexa.local` | `ROLE_BUYER` | Buyer portal access |
| `sales@nexa.com` | `ROLE_SALES` | Commercial workspace profile |
| `logistics@nexa.com` | `ROLE_LOGISTICS` | Logistics workspace profile |
| `warehouse@nexa.com` | `ROLE_WAREHOUSE` | Warehouse workspace profile |
| `buyer@nexa.com` | `ROLE_BUYER` | B2B buyer workspace profile |

Workspace password:

```text
NexaAccess2026!
```
