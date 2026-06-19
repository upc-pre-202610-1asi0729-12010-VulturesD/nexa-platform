# Security Policy

## Scope

This repository contains the Nexa Platform REST API built with Java, Spring Boot, Spring Security, JWT, Spring Data JPA, H2, and PostgreSQL.

| Area | Status |
|---|---|
| REST API under `/api/v1` | In scope |
| Authentication and JWT handling | In scope |
| PostgreSQL/H2 persistence configuration | In scope |
| Render deployment configuration | In scope |
| WebApp UI issues | Report in `nexa-webapp` |
| Landing website issues | Report in `nexa-website` |

## Reporting A Vulnerability

Do not open a public issue for vulnerabilities. Contact the maintainers privately with:

- affected endpoint or file,
- reproduction steps,
- expected impact,
- suggested fix if available.

## Secret Handling

- Do not commit `.env` files or database credentials.
- Render credentials must be supplied through environment variables.
- `JWT_SECRET` must be configured as a runtime secret outside Git.

---

Team Nexa · Course 1ASI0729 · 2026-10
