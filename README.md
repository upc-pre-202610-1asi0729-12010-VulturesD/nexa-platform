<div align="center">

<br/>

<img src="https://raw.githubusercontent.com/upc-pre-202610-1asi0729-12010-VulturesD/nexa-website/main/nexa.svg" alt="Nexa" width="200"/>

<br/><br/>

# nexa-platform

**Backend API and platform services for the Nexa B2B distribution platform**

<br/>

![ASP.NET Core](https://img.shields.io/badge/ASP.NET%20Core-512BD4?style=for-the-badge&logo=dotnet&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

<br/>

![Course](https://img.shields.io/badge/Course-1ASI0729%20Desarrollo%20de%20Aplicaciones%20Open%20Source-0a2540?style=flat-square)
![Cycle](https://img.shields.io/badge/Cycle-2026--10-0a2540?style=flat-square)
![University](https://img.shields.io/badge/University-UPC-0a2540?style=flat-square)
![Team](https://img.shields.io/badge/Team-Vultures%20Devs-2a67d9?style=flat-square)
![Status](https://img.shields.io/badge/Status-In%20Development-f59e0b?style=flat-square)

<br/>

</div>

---

## What is this repository?

This repository contains the backend API and platform services for the Nexa B2B cold-chain distribution platform. Built with ASP.NET Core Web API following a Domain-Driven Design (DDD) architecture, it exposes RESTful endpoints for authentication, product catalog management, inventory monitoring, B2B order processing, and cold-chain traceability.

---

## Architecture

The platform is organized around the following bounded contexts:

| Bounded Context | Responsibility |
|---|---|
| Identity | Authentication, authorization, and user session management |
| Catalog | Product definitions, categories, and pricing structures |
| Inventory | Stock levels, warehouse locations, and cold-chain conditions |
| Orders | B2B order lifecycle from quote to fulfillment |
| Customer Management | Distributor and client account management |
| Commercial Conditions | Contract terms, pricing agreements, and credit lines |
| Traceability | Cold-chain audit trail and condition monitoring |

---

## Tech stack

| Layer | Technology |
|---|---|
| Framework | ASP.NET Core Web API |
| Language | C# |
| ORM | Entity Framework Core |
| Database | MySQL |
| Auth | OAuth 2.0 / JWT |
| External integrations | Stripe, Google Notifications, Cloud Storage |

---

## Branching strategy

| Branch | Purpose |
|---|---|
| `main` | Stable, reviewed releases |
| `develop` | Integration branch |
| `feature/*` | New features and modules |
| `fix/*` | Bug fixes and corrections |

All commits follow [Conventional Commits 1.0.0](https://www.conventionalcommits.org/): `type(scope): description`.

---

## Related repositories

| Repository | Description |
|---|---|
| [nexa-report](https://github.com/upc-pre-202610-1asi0729-12010-VulturesD/nexa-report) | Academic report (Docs-as-Code) |
| [nexa-website](https://github.com/upc-pre-202610-1asi0729-12010-VulturesD/nexa-website) | Landing page (HTML5 / CSS3 / JS) |
| [nexa-webapp](https://github.com/upc-pre-202610-1asi0729-12010-VulturesD/nexa-webapp) | Web application (Vue.js) |

---

## Team

**Organization:** [upc-pre-202610-1asi0729-12010-VulturesD](https://github.com/upc-pre-202610-1asi0729-12010-VulturesD)

| Code | Member | Role |
|---|---|---|
| U202323040 | Yucra Sandoval, Diego Sebastian | Team Leader |
| U202411937 | Marín Cueva, César Fernando | Team Member |
| U20241A054 | Verde Bueno, Joaquín Francisco | Team Member |
| U202416289 | Torrejón De Los Santos, Gino Rodrigo | Team Member |
| U202413142 | Rojas Mancilla, Gerard Gianpier | Team Member |

---

<div align="center">

<br/>

**Nexa** · Universidad Peruana de Ciencias Aplicadas · 2026-10

*1ASI0729 — Desarrollo de Aplicaciones Open Source · Ingeniería de Software*

<br/>

</div>
