# MySQL Local Runbook

## Environment

```bash
export SPRING_PROFILES_ACTIVE=mysql
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=nexa_platform_db
export DB_USERNAME=nexa_user
export DB_PASSWORD='<local-password>'
export JWT_SECRET='<at-least-32-characters-local-secret>'
```

## Run

```bash
./mvnw clean test
SPRING_PROFILES_ACTIVE=mysql ./mvnw spring-boot:run
```

## Smoke Checks

```bash
curl -i http://localhost:8080/api/v1/health
curl -i http://localhost:8080/v3/api-docs
curl -i -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"sales@nexa.com","password":"NexaDemo2026!"}'
```

Use the returned bearer token for protected endpoints:

```bash
curl -i http://localhost:8080/api/v1/catalog-items \
  -H "Authorization: Bearer $TOKEN"
```

## Notes

- The MySQL profile uses `ddl-auto=update` for academic/local deployment readiness.
- Local/test H2 profiles use `ddl-auto=create-drop`.
- Existing MySQL data is not deleted by the seed. If an old local database contains previous demo products, clean it manually only after backing it up and confirming the reset.
- Do not commit real local passwords or JWT secrets.
