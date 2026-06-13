# Prompt 03 Task Board - Platform Contract Alignment

Branch: `feature/platform-endpoint-database-assets-alignment`

## Tasks

| Task | Status | Evidence |
|---|---|---|
| Confirm repository state and previous reports | Done | Clean worktree on Prompt 03 branch. |
| Compare WebApp expected endpoints with Platform controllers | Done | `docs/api-contract-matrix.md`. |
| Align required WebApp endpoint aliases and resources | Done | `/api/v1/catalog-items` controller and contract tests. |
| Align catalog seed data and image URL contract | Done | `src/main/resources/data/catalog-items.json`, catalog tests, docs. |
| Align IAM profile/auth response contract | Done | Source profile seeds and auth flow tests. |
| Document MySQL/H2 readiness and database seed data | Done | `docs/database-design.md`, `docs/mysql-local-runbook.md`, seed docs. |
| Verify OpenAPI, tests, package, and WebApp smoke | Done | Maven test/package, curl, and Playwright WebApp smoke passed. |
| Write Prompt 03 report | Pending | `docs/audit/platform-api-alignment-report.md` pending. |

## Guardrails

- No push.
- No destructive database or Git commands.
- No committed secrets.
- Keep Spring Boot + Java + Angular contract alignment.
- Preserve professor/CatchUp-style resources and assemblers where already present.
