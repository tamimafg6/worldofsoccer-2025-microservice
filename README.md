# World of Soccer 2025 — Microservices

A small Spring Boot **microservices** project for managing soccer data (**teams, leagues, locations, matches**) behind an API Gateway. Cleanly structured for local dev or Docker Compose.

---

## Table of Contents

- [Services](#services)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Quick Start (Local, no Docker)](#quick-start-local-no-docker)
- [Run Everything with Docker](#run-everything-with-docker)
- [Calling the APIs](#calling-the-apis)
- [Testing](#testing)
- [Common Gradle Tasks](#common-gradle-tasks)
- [Environment & Configuration](#environment--configuration)
- [Tips for Development](#tips-for-development)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)
- [Author](#author)

---

## Services

- **api-gateway** — Routes external traffic to backend services
- **teams-service** — CRUD for teams
- **league-service** — CRUD for leagues
- **location-service** — Stadiums / cities / venues
- **match-service** — Matches and scheduling

> The repository includes a **Gradle wrapper** and a top-level **docker-compose.yml** to run everything together.

---

## Tech Stack

- **Language:** Java (Spring Boot 3.x suggested)
- **Build:** Gradle (via wrapper `./gradlew`)
- **Runtime:** JVM 17+
- **Container:** Docker & Docker Compose (optional but supported)

---

## Project Structure

```text
worldofsoccer-2025-microservice/
├─ api-gateway/           # Edge service / routes to backend services
├─ teams-service/         # CRUD for teams
├─ league-service/        # CRUD for leagues
├─ location-service/      # Stadiums / cities / venues
├─ match-service/         # Matches and scheduling
├─ Documents/             # (Docs/notes if any)
├─ docker-compose.yml     # Run the whole stack with Docker
├─ gradlew / gradlew.bat  # Gradle wrapper (no local Gradle needed)
├─ settings.gradle        # Multi-project settings
├─ test_all.bash          # Script to run all tests
└─ create-projects.bash   # Helper script for subprojects
```

---

## Prerequisites

- **JDK 17+** (recommended for Spring Boot 3.x)
- **Git**
- **Docker & Docker Compose** (only if you want to run the stack in containers)
- You **do not** need a local Gradle install — use the wrapper: `./gradlew`

---

## Quick Start (Local, no Docker)

From the project root:

1. **Build all services**
   ```bash
   ./gradlew clean build
   ```

2. **Run one service (example: Teams)**
   ```bash
   cd teams-service
   ../gradlew bootRun
   ```

3. **Run another service in a new terminal (example: League)**
   ```bash
   cd league-service
   ../gradlew bootRun
   ```

4. **Run the API Gateway**
   ```bash
   cd ../api-gateway
   ../gradlew bootRun
   ```

**Notes**  
- Default Spring Boot port is `8080`.  
- If services define custom ports in `application.properties` / `application.yml`, use those (check each service’s `src/main/resources`).

---

## Run Everything with Docker

From the repo root:

1. **Build and start all services**
   ```bash
   docker compose up --build
   ```

2. **Stop and remove containers**
   ```bash
   docker compose down
   ```

The top-level `docker-compose.yml` builds and starts the gateway plus all services.

---

## Calling the APIs

The **API Gateway** routes requests to each backend service. Typical patterns (adjust to actual routes/ports):

- `GET /api/teams`, `POST /api/teams`
- `GET /api/leagues`, `POST /api/leagues`
- `GET /api/locations`, `POST /api/locations`
- `GET /api/matches`, `POST /api/matches`

If you’re unsure of ports or paths, check:
- Each service’s `application.properties` / `application.yml`
- The gateway’s route configuration
- The `docker-compose.yml` port mappings

---

## Testing

- **Run the full test suite**
  ```bash
  ./gradlew test
  ```

- **Convenience script at the repo root**
  ```bash
  ./test_all.bash
  ```

---

## Common Gradle Tasks

```bash
# Build everything
./gradlew build

# Run a specific service (from its directory)
../gradlew bootRun

# Verify (and format, if you add plugins later)
./gradlew check
```

---

## Environment & Configuration

Put service-specific settings in `src/main/resources/application.yml` (or `.properties`) for each microservice. Common examples:

- `server.port=...`
- Database URLs / credentials (if/when you add a DB)
- CORS and gateway route definitions
- Management endpoints (e.g., Spring Boot Actuator)

---

## Tips for Development

- Start backend services first, then the **API Gateway**.  
- If a route returns **404** through the gateway, try the service directly (e.g., `http://localhost:<service-port>/...`) to isolate the issue.  
- Keep endpoints small and focused (start with CRUD, then add relationships).  
- Add `/actuator/health` to quickly verify services are up (via Spring Boot Actuator).

---

## Troubleshooting

- **Port already in use:** change `server.port` in the service’s `application.yml` or stop the process holding the port.  
- **Gateway 404 / 502:** double-check route paths and target service ports; confirm the service is actually running.  
- **Docker build cache issues:** run with `--no-cache` or `docker compose build --no-cache`.  
- **Gradle memory:** you can add `org.gradle.jvmargs=-Xmx2g` in `gradle.properties` if builds are memory constrained.

---

## Contributing

1. Fork the repository  
2. Create a feature branch: `git checkout -b feature/my-change`  
3. Commit: `git commit -m "feat: add X"`  
4. Push: `git push origin feature/my-change`  
5. Open a Pull Request

---

## License

Choose a license (MIT, Apache-2.0, etc.) and add a `LICENSE` file at the repo root if you plan to share or reuse this code publicly.

---

## Author

**Tamim Afghanyar** — 2025
