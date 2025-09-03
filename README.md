# World of Soccer 2025 — Microservices

A small Spring Boot microservices project for managing soccer data (teams, leagues, locations, and matches) behind an API gateway.

Services in this repo: api-gateway, teams-service, league-service, location-service, match-service. The repo also includes Gradle wrapper and a docker-compose.yml for running everything together. 
GitHub

#  Project Structure
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


Languages: mostly Java with a bit of Shell and Dockerfile content. 
GitHub

# Prerequisites

- JDK 17+ (recommended for Spring Boot 3.x projects)

- Git

- Docker & Docker Compose (only if you want to run the stack via containers)

- You don’t need to install Gradle; the repository ships with the Gradle wrapper (./gradlew). 
GitHub

#  Quick Start (Local, no Docker)

From the project root:

1. Build all services

./gradlew clean build


2. Run one service (example: Teams)

cd teams-service
../gradlew bootRun


3. Run another service in a new terminal (example: League)

cd league-service
../gradlew bootRun


4. Run the API Gateway

cd ../api-gateway
../gradlew bootRun


- Default Spring Boot port is 8080.
- If services define custom ports in application.properties/application.yml, use those. (Check each service’s src/main/resources.)

#  Run Everything with Docker

From the repo root:

docker compose up --build


This uses the top-level docker-compose.yml to build and start the gateway + all services. Stop with Ctrl+C, then:

docker compose down


The compose file lives at the repository root. 
GitHub

#  Calling the APIs

- API Gateway will route requests to each backend service.

- Typical patterns (adjust to your configured routes/ports):

GET /api/teams, POST /api/teams

GET /api/leagues, POST /api/leagues

GET /api/locations, POST /api/locations

GET /api/matches, POST /api/matches

If you’re unsure of ports or paths, check:

- Each service’s application.properties / application.yml

- The gateway’s route config

- The docker-compose.yml port mappings

#  Testing

Run the full test suite:

./gradlew test


There’s also a convenience script at the repo root:

./test_all.bash


The test_all.bash helper exists at the top level. 
GitHub

# Common Gradle Tasks
# Build everything
./gradlew build

# Run a specific service (from its directory)
../gradlew bootRun

# Format/verify (if you add plugins later)
./gradlew check

 Environment & Configuration

- Put service-specific settings in src/main/resources/application.yml (or .properties) per microservice.

- Common examples:

server.port=...

Database URLs / credentials (if/when you add a DB)

CORS and gateway route definitions

# Tips for Development

- Start backend services first, then the gateway.

- If a route returns 404 through the gateway, try the service directly (e.g., http://localhost:<service-port>/...) to isolate the issue.

- Keep endpoints small and focused (CRUD first, then relationships).

- Add /actuator/health to quickly verify services are up (via Spring Boot Actuator).

#  Contributing

1. Fork the repo

2. Create a feature branch: git checkout -b feature/my-change

3. Commit: git commit -m "feat: add X"

4. Push: git push origin feature/my-change

5. Open a PR

#  License

Pick a license (MIT/Apache-2.0/etc.) and add a LICENSE file at the repo root if you plan to share or reuse this code publicly.

#  Author

Tamim Afghanyar — 2025
