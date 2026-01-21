# Varaamo (Backend)

Spring Boot backend for a conference room reservation system.

## Prerequisites

- Java 21
- No Maven install required (uses Maven Wrapper)

If `./mvnw` complains about `JAVA_HOME`, set it to your JDK path (example below).

## Build & Test (Windows PowerShell)

```powershell
cd C:\Varaamo
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\mvnw test
```

## Run (Windows PowerShell)

```powershell
cd C:\Varaamo
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\mvnw spring-boot:run
```

## Frontend (Vite + React + TypeScript)

The frontend lives in the `frontend/` folder and uses a Vite dev-server proxy to call the backend (no CORS setup needed).

```powershell
cd C:\Varaamo\frontend
npm install
npm run dev
```

- Frontend: `http://localhost:5173/`
- Backend APIs used by the UI (proxied in dev): `GET/POST http://localhost:8080/rooms`, `GET/POST/DELETE http://localhost:8080/reservations`

## Useful URLs (when running)

- Health endpoint: `GET http://localhost:8080/api/health`
- Actuator health: `GET http://localhost:8080/actuator/health`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- H2 console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:varaamo`
  - User: `sa`
  - Password: (empty)
