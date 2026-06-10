# Deployment Notes

Build the API artifact:

```bash
./mvnw package
```

Run with MySQL configuration:

```bash
SPRING_PROFILES_ACTIVE=mysql java -jar target/nexa-platform-0.6.1.jar
```

Secrets must be provided through environment variables. Do not commit runtime credentials.
