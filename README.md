## Why This Project Exists

Modern social platforms need a dedicated service to handle user identity and social graphs independently from other domains. This service owns the full lifecycle of user accounts (registration, login, password management) and all social relationships (friendships, follow/unfollow, block/unblock), publishing domain events via Kafka so other services can react without tight coupling.

## What It Does

- **Authentication** — Register, login, JWT-based access/refresh tokens, password reset via email
- **User Profile** — Full name, profile picture, update profile
- **Session Management** — Revoke all sessions across devices
- **Friendships** — Send/accept/reject/cancel friend requests, list friends, friend suggestions
- **Social Connections** — Follow/unfollow users, block/unblock users, paginated follower/following/blocked lists
- **Event-Driven** — Publishes follow, unfollow, block, and unblock events to Kafka topics
- **Emails** — Sends welcome and password-reset emails via AWS SES templates

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.4, WebFlux (reactive) |
| Language | Java 17 |
| Relational DB | PostgreSQL 16 (R2DBC for reactive, JDBC for Flyway migrations) |
| Graph DB | Neo4j 5 (social graph — friendships, follows, blocks) |
| Cache | Redis 7 (reactive, with TTL-based caching for users, friends, suggestions) |
| Messaging | Apache Kafka (with Reactor Kafka for non-blocking produce) |
| Auth | JWT (access + refresh + reset tokens) |
| Email | AWS SES (templated emails) |
| Secrets | AWS Secrets Manager (production DB/Kafka credentials) |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Build | Gradle |
| Code Quality | Spotless (Google Java Format), Lombok, MapStruct |
| Testing | JUnit 5, Testcontainers (PostgreSQL, Neo4j, Kafka, Redis, LocalStack) |

## Prerequisites

- Java 17+
- Docker & Docker Compose

## Getting Started

### 1. Start infrastructure

```bash
docker-compose up -d
```

This starts PostgreSQL, Neo4j, Redis, Kafka (with Zookeeper), and LocalStack (SES).

LocalStack automatically initializes SES with a verified sender email and email templates via `localstack/init/ses-init.sh`. No manual setup needed.

### 2. Run the application

```bash
./gradlew bootRun --args='--spring.profiles.active=localdev'
```

The server starts on **http://localhost:8090**.

### 3. Explore the API

| URL | Description |
|---|---|
| http://localhost:8090/swagger-ui.html | Swagger UI (interactive API explorer) |
| http://localhost:8090/v3/api-docs | OpenAPI 3.0 JSON specification |

## API Overview

### Auth (`/v1/api/auth`) — public, no token required

| Method | Endpoint | Description |
|---|---|---|
| POST | `/register` | Register a new user |
| POST | `/login` | Login and receive JWT tokens |
| POST | `/forgot-password` | Request a password reset email |
| POST | `/reset-password` | Reset password with token |
| POST | `/validate-token` | Validate a JWT token |
| POST | `/refresh` | Refresh an access token |
| POST | `/logout` | Logout (invalidate refresh token) |

### User (`/v1/api/me`) — requires Bearer token

| Method | Endpoint | Description |
|---|---|---|
| GET | `/profile` | Get current user's profile |
| PUT | `/profile` | Update full name and/or profile picture |
| POST | `/change-password` | Change current user's password |
| GET | `/sessions?currentRefreshToken=...` | List all active sessions (marks current) |
| POST | `/sessions/revoke-others` | Revoke all sessions except current |
| POST | `/sessions/revoke` | Revoke a specific session (not current) |

### Friendships (`/v1/api/friendships`) — requires Bearer token

| Method | Endpoint | Description |
|---|---|---|
| POST | `/requests` | Send a friend request |
| POST | `/requests/{id}/accept` | Accept a friend request |
| POST | `/requests/{id}/reject` | Reject a friend request |
| POST | `/requests/{id}/cancel` | Cancel a sent friend request |
| GET | `/requests/pending` | List pending friend requests |
| GET | `/requests/sent` | List sent friend requests |
| GET | `/friends` | List friends |
| GET | `/suggestions` | Get friend suggestions |

### Social (`/v1/api/social`) — requires Bearer token

| Method | Endpoint | Description |
|---|---|---|
| POST | `/follow/{userId}` | Follow a user |
| POST | `/unfollow/{userId}` | Unfollow a user |
| GET | `/following` | List users you follow (paginated) |
| GET | `/followers` | List your followers (paginated) |
| POST | `/block/{userId}` | Block a user |
| POST | `/unblock/{userId}` | Unblock a user |
| GET | `/blocked` | List blocked users (paginated) |

## Project Structure

```
src/main/java/com/backend/users/
├── controllers/          # REST controllers (Auth, User, Friendship, Social)
├── services/             # Business logic
├── repositories/         # R2DBC (PostgreSQL) + Neo4j repositories
├── entities/             # PostgreSQL entities
├── graph/                # Neo4j node and relationship models
├── dtos/                 # Request/response DTOs
├── enums/                # Enums (FriendRequestStatus, JwtPayloadFields)
├── mappers/              # MapStruct mappers
├── security/             # Spring Security config, JWT filter
├── cache/                # Redis cache config and properties
├── kafka/                # Kafka producer, event properties, health check
├── ses/                  # AWS SES email service and config
├── neo4j/                # Neo4j driver config, connection settings
├── postgresql/           # R2DBC connection factory, read/write routing
├── openapi/              # OpenAPI/Swagger configuration
└── utils/                # JWT utilities, constants
```
