# Cupcake

Academic web application for a cupcake store, developed as part of a Software Engineering project at UNICSUL.

The project provides a REST API for product and order management, using Java 21, Spring Boot 4.1.2, and PostgreSQL.

## 1. Technologies

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 4.1.2 | Application framework |
| Spring Web MVC | REST API |
| Spring Data JPA | Data persistence |
| Hibernate | Object-relational mapping |
| PostgreSQL | Main database |
| Flyway | Database migrations |
| Maven | Build and dependency management |
| JUnit 5 | Automated testing |
| Mockito | Unit test dependency isolation |
| Testcontainers | PostgreSQL integration testing |
| H2 | In-memory testing database |
| Git | Version control |

## 2. Architecture

The application follows a **Modular Monolith + Layered Architecture**.

The source code is organized into three main modules:

- `product` — product management.
- `order` — order management.
- `shared` — shared components, including exception handling.

Each functional module separates responsibilities into:

- Controller
- DTO
- Entity
- Repository
- Service

The application uses DTOs at REST API boundaries and does not expose JPA entities directly.

## 3. Implemented Features

### Products

- Create products.
- Retrieve active products.
- Retrieve a product by ID.
- Update product information.
- Deactivate products.
- Paginated product listing.
- Product sorting.

### Orders

- Create orders.
- Retrieve an order by ID.
- Paginated order listing.
- Calculate item subtotals and order totals.
- Preserve product names and unit prices at purchase time.
- Consolidate repeated product IDs in an order request.

### Error Handling

- Centralized exception handling.
- HTTP Problem Details responses.
- Request validation.
- Resource-not-found handling.
- Business-rule violation handling.

## 4. Project Structure

```text
cupcake-unicsul/
├── docs/
│   ├── d01-requirements/
│   ├── ...
│   └── d16-documentation/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
│       ├── java/
│       └── resources/
├── pom.xml
├── README.md
└── LEIAME.md
```

## 5. Prerequisites

To run the application locally, you need:

- Java 21 JDK.
- Maven.
- PostgreSQL.
- Docker for integration tests using Testcontainers.

## 6. Database Configuration

The development profile uses PostgreSQL.

Default connection:

```text
jdbc:postgresql://localhost:5432/cupcake
```

Configure the following environment variables:

```bash
export DB_USERNAME=cupcake_user
export DB_PASSWORD='your_password'
```

Create the database and configure the PostgreSQL user before starting the application.

Flyway automatically manages database migrations during application startup.

## 7. Running the Application

Start the application using the development profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Alternatively, build the executable JAR:

```bash
mvn clean package
```

Run the generated artifact:

```bash
java -jar target/cupcake-0.0.1-SNAPSHOT.jar \
    --spring.profiles.active=dev
```

## 8. Running Tests

Execute the complete test suite:

```bash
mvn clean test
```

Run a specific test class:

```bash
mvn test -Dtest=ProductTest
```

Integration tests using Testcontainers require Docker to be available.

Test execution reports are generated under:

```text
target/surefire-reports/
```

## 9. REST API

The API base path is:

```text
/api/v1
```

Main resources:

| HTTP Method | Endpoint | Description |
|---|---|---|
| GET | `/products` | List active products |
| GET | `/products/{id}` | Retrieve a product |
| POST | `/products` | Create a product |
| PUT | `/products/{id}` | Update a product |
| DELETE | `/products/{id}` | Deactivate a product |
| GET | `/orders` | List orders |
| GET | `/orders/{id}` | Retrieve an order |
| POST | `/orders` | Create an order |

For request and response details, see the API documentation.

## 10. Documentation

Technical documentation is maintained under `docs/d16-documentation/`.

Available documents:

| Document | Description |
|---|---|
| `architecture.md` | System architecture (English) |
| `architecture-pt-br.md` | Arquitetura do sistema |
| `api.md` | REST API documentation (English) |
| `api-pt-br.md` | Documentação da API REST |
| `database.md` | Documentação do banco de dados |
| `development-setup.md` | Configuração do ambiente |
| `testing.md` | Estratégia de testes |

Earlier project phases and UML diagrams are maintained under the corresponding documentation directories.

## 11. Current Limitations

The current implementation does not include:

- User authentication or authorization.
- Payment integration.
- Image upload.
- Persistent shopping cart.
- Order status management.
- Order cancellation.
- Frontend implementation.
- Order ownership verification.

The API should not be exposed publicly without addressing the relevant security limitations, particularly access to customer information through order endpoints.

## 12. Academic Project

This application is an academic project developed to apply Software Engineering concepts, including requirements analysis, UML modeling, software architecture, database design, implementation, and automated testing.
