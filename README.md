# Library Management System

A secure REST API for managing a library's catalog, users, and lending workflow. The application supports role-based access for administrators and members, JWT authentication, book availability tracking, loan returns, overdue fines, automated tests, and interactive API documentation.

## Project Status

> The backend API is functional and documented. The next development phase focuses on automation, containerization, and the frontend application.

| Area | Status |
|---|---|
| REST API | Complete |
| PostgreSQL integration | Complete |
| JWT authentication | Complete |
| Role-based authorization | Complete |
| Validation and exception handling | Complete |
| Unit and security tests | 63 tests passing |
| Swagger / OpenAPI documentation | Complete |
| CI/CD pipeline | Complete |
| Docker support | Planned |
| Frontend application | Planned |
| Production deployment | Not started |

## Features

- User registration and login with JWT authentication
- `ADMIN` and `MEMBER` authorization roles
- Book, author, and category management
- Exact and partial catalog searches
- Book borrowing and return workflows
- Personal loan endpoints with ownership validation
- Active-loan and borrowing-history tracking
- Book availability management
- Due-date and late-return fine calculation
- Request validation and duplicate-resource protection
- Centralized error handling with meaningful HTTP status codes
- Interactive Swagger UI documentation
- Automated service, security, JWT, and controller tests

## Technology Stack

- Java 17
- Spring Boot 4
- Spring MVC
- Spring Security
- JSON Web Tokens (JWT)
- Spring Data JPA and Hibernate
- PostgreSQL
- Bean Validation
- JUnit 5, Mockito, and MockMvc
- Swagger / OpenAPI (springdoc-openapi)
- Maven

## Architecture

The backend follows a layered architecture:

```text
HTTP Request
    |
Controller  -> request validation and HTTP responses
    |
Service     -> business rules and workflows
    |
Repository  -> database access through Spring Data JPA
    |
PostgreSQL
```

DTOs separate the public API models from the persistence entities, while a global exception handler produces consistent error responses.

## API Overview

The API currently provides **35 endpoints** organized into the following areas:

| Resource | Base path | Main operations |
|---|---|---|
| Authentication | `/auth` | Register and log in |
| Books | `/books` | CRUD and title search |
| Authors | `/authors` | CRUD and name search |
| Categories | `/categories` | CRUD |
| Loans | `/loans` | Borrow, return, history, and active loans |
| Users | `/users` | Administrative user management |

Most endpoints require a JWT. Book, author, category, global-loan, and user mutations are restricted according to the configured `ADMIN` and `MEMBER` permissions.

## API Documentation

After starting the application, open:

- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

To test a protected endpoint in Swagger:

1. Use `POST /auth/login` to obtain a token.
2. Select **Authorize**.
3. Paste the JWT value into the `bearerAuth` field.
4. Execute the protected request.

Do not add the `Bearer` prefix manually in the Swagger authorization dialog.

## Getting Started

### Prerequisites

- Java 17 or newer
- Maven 3.9 or newer
- PostgreSQL
- Git

### 1. Clone the repository

```bash
git clone https://github.com/MarMecha/Library-Management-System.git
cd Library-Management-System/LibraryManagementSystem
```

### 2. Create the database

Create a PostgreSQL database and configure its connection in:

```text
src/main/resources/application.properties
```

Use local credentials or environment variables. Never commit real passwords or production secrets.

Example configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/LibraryManagementSystem
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

jwt.secret=${JWT_SECRET}
```

### 3. Configure environment variables

PowerShell example:

```powershell
$env:DB_USERNAME="your-postgresql-username"
$env:DB_PASSWORD="your-postgresql-password"
$env:JWT_SECRET="your-base64-encoded-secret"
```

The JWT secret must be a strong Base64-encoded value. Do not reuse the example test secret or commit a real secret to Git.

### 4. Run the application

```bash
mvn spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

## Running the Tests

Ensure the required environment variables are available, then run:

```bash
mvn test
```

The current automated test suite covers:

- Service business logic
- Validation and exceptional paths
- JWT creation and validation
- JWT authentication filtering
- Authentication flows
- Controller authorization rules
- Loan ownership and return behavior

## Security

- Stateless JWT authentication
- BCrypt password hashing
- Role-based endpoint authorization
- Ownership checks for member loan operations
- Request validation
- Restricted administrative operations
- Secrets excluded from API responses and documentation

Secrets and database credentials must be provided through environment variables in shared or production environments.

## Roadmap

### Next step

- [x] Add a GitHub Actions workflow that builds the project and runs all tests on every push and pull request

### Planned

- [ ] Add Docker support for the Spring Boot API
- [ ] Add Docker Compose for the API and PostgreSQL
- [ ] Develop a responsive frontend application
- [ ] Add frontend authentication and role-based navigation
- [ ] Connect the frontend to the REST API
- [ ] Add integration tests with a dedicated test database
- [ ] Add database migrations with Flyway or Liquibase
- [ ] Add structured logging and production monitoring
- [ ] Deploy the database, backend, and frontend

## Current Development Phase

The project has completed its core backend phase:

- Domain model and persistence
- Business logic
- REST controllers and DTOs
- Authentication and authorization
- Error handling and validation
- Automated tests
- OpenAPI documentation

The recommended next milestone is **continuous integration with GitHub Actions**, followed by Dockerization and frontend development.

## Author

Developed by [MarMecha](https://github.com/MarMecha).
