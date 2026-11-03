# Testing Guide

This document describes the testing approach and commands for the Cupcake application.

## 1. Testing objectives

The test suite is intended to verify domain rules, application behavior, persistence behavior, and important integration paths.

The strategy prioritizes meaningful scenarios over test quantity or a target code-coverage percentage. Tests should focus on observable behavior rather than implementation details.

## 2. Testing technologies

The project uses the following testing technologies:

- **JUnit 5** for test organization and assertions.
- **Mockito** for isolated tests with mocked dependencies.
- **Spring Boot Test** for Spring application testing support.
- **Spring Data JPA Test** for persistence-layer tests.
- **Spring Web MVC Test** for controller and HTTP behavior tests.
- **H2** as the in-memory database for tests that do not require PostgreSQL-specific behavior.
- **Testcontainers** with PostgreSQL for integration tests against a real PostgreSQL instance.

## 3. Test categories

### 3.1 Unit tests

Unit tests verify individual classes without starting the complete application context.

Examples include domain behavior and validation of business invariants.

### 3.2 Service tests

Service tests verify application use cases, including interactions with repositories and enforcement of business rules. Mockito is used where dependency isolation is appropriate.

### 3.3 Repository tests

Repository tests verify persistence operations and query behavior. H2 is used for suitable tests; PostgreSQL-specific behavior is verified through PostgreSQL integration tests.

### 3.4 Integration tests

Integration tests verify important paths across application components and persistence using PostgreSQL through Testcontainers.

The PostgreSQL integration tests use the `postgres:16-alpine` image. Docker must be available when these tests run.

## 4. Business rules covered

The tests include important product and order rules, such as:

- Product prices must be positive.
- Product details must satisfy the domain constraints before an update is applied.
- Updating product details must not change its active/inactive status.
- Inactive products must not appear in active-product queries.
- Inactive products cannot be added to an order.
- Order quantities must be positive.
- An order must contain at least one item.
- Repeated product identifiers in an order request are consolidated by the order service.
- Order item prices and product names are snapshots of the values used when the order is created.
- Order totals and item subtotals are calculated by the backend.

The backend retrieves current product prices from the database; it does not rely on prices supplied by the client.

## 5. Test configuration

The test configuration is located at:

```text
src/test/resources/application-test.yml
```

It configures H2 with the following JDBC URL:

```text
jdbc:h2:mem:cupcake_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1
```

The test profile uses:

- H2 in PostgreSQL compatibility mode.
- Hibernate `create-drop` for the test schema.
- Flyway disabled.

This configuration is suitable for tests that do not require PostgreSQL-specific database behavior.

PostgreSQL integration tests use Testcontainers and the `postgres:16-alpine` image instead of treating H2 as a complete substitute for PostgreSQL.

## 6. Test source organization

Tests are located under:

```text
src/test/java
```

Test-specific application configuration is located under:

```text
src/test/resources/application-test.yml
```

Keep tests close to the package structure of the application so that their responsibilities are easy to identify.

## 7. Running tests

Run the complete test suite:

```bash
mvn clean test
```

Run tests without cleaning first:

```bash
mvn test
```

Run a single test class:

```bash
mvn test -Dtest=ProductTest
```

Run selected test classes:

```bash
mvn test -Dtest=ProductTest,ProductRepositoryTest
```

Run the PostgreSQL integration test class:

```bash
mvn test -Dtest=PostgreSqlIntegrationTest
```

The PostgreSQL integration test command requires Docker to be running and accessible.

## 8. Reviewing test results

Maven Surefire reports are generated under:

```text
target/surefire-reports/
```

Review the reports when a test fails. Use the first relevant failure and its stack trace to identify the failing layer before changing code.

## 9. Test strategy status

At the completion of Phase 15, the reported test run contained:

- 97 tests executed.
- 0 failures.
- 0 errors.
- 0 skipped tests.
- `BUILD SUCCESS`.

This is a historical result for that phase, not a permanent test-count guarantee. The number of tests may change as the project evolves.

The project does not currently use JaCoCo to enforce a code-coverage percentage. The strategy emphasizes whether important behavior, boundary conditions, and business rules are tested effectively.
