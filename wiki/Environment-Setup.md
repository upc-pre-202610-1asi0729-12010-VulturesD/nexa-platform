# Environment Setup

## Local

```bash
./mvnw test
./mvnw package
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

API:

```text
http://localhost:8080/api/v1
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

## Render

Use:

```text
SPRING_PROFILES_ACTIVE=postgres,seed
DATABASE_URL=<managed PostgreSQL connection string>
FRONTEND_ORIGIN=https://nexa-webapp-fv2v.onrender.com
```
