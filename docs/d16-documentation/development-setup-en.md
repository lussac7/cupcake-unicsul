# Development Setup

This guide explains how to prepare a local development environment for the Cupcake application.

## 1. Prerequisites

Install the following tools:

- **JDK 21** — required to compile and run the application.
- **Maven** — used to build, test, and run the project.
- **PostgreSQL** — used by the development profile.
- **Git** — used to clone and manage the repository.
- **Docker** — required for the PostgreSQL Testcontainers integration tests.
- An IDE is optional. Any editor that supports Java and Maven can be used.

Verify the Java and Maven installations:

```bash
java -version
javac -version
mvn -version
```

The Java runtime and compiler should report version 21.

## 2. Obtain the source code

Clone the repository using its configured remote URL:

```bash
git clone <repository-url>
cd cupcake-unicsul
```

Replace `<repository-url>` with the repository URL.

## 3. Configure PostgreSQL

The development profile expects a PostgreSQL database at:

```text
jdbc:postgresql://localhost:5432/cupcake
```

Create a database and a dedicated user. For example, connect to PostgreSQL as an administrative user and execute:

```sql
CREATE USER cupcake_user WITH PASSWORD 'change-this-password';
CREATE DATABASE cupcake OWNER cupcake_user;
```

Use a password appropriate for your local environment. Do not commit credentials to source control.

## 4. Configure environment variables

The development configuration reads database credentials from:

- `DB_USERNAME`
- `DB_PASSWORD`

Set them in the shell before starting the application. For example:

```bash
export DB_USERNAME=cupcake_user
export DB_PASSWORD='your-local-password'
```

The application configuration is split across these files:

| File | Purpose |
|---|---|
| `src/main/resources/application.yml` | Common application configuration |
| `src/main/resources/application-dev.yml` | Development PostgreSQL configuration |
| `src/test/resources/application-test.yml` | Test configuration using H2 |

The common configuration disables Open Session in View, uses Hibernate schema validation, enables Flyway, and configures application logging.

The development profile uses PostgreSQL and enables DEBUG logging for the project packages.

## 5. Run the application

Start the application with the development profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The application uses the configured local PostgreSQL database. Flyway applies the available database migrations during startup.

## 6. Build the application

Compile the source code:

```bash
mvn clean compile
```

Create the application package:

```bash
mvn clean package
```

The generated executable JAR is:

```text
target/cupcake-0.0.1-SNAPSHOT.jar
```

Run the packaged application with the development profile:

```bash
java -jar target/cupcake-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

Ensure that the required database environment variables are available to the process.

## 7. Run tests

Execute the test suite:

```bash
mvn clean test
```

The test profile uses an in-memory H2 database configured in PostgreSQL compatibility mode. Hibernate creates and drops the test schema; Flyway is disabled for this profile.

PostgreSQL integration tests use Testcontainers with the `postgres:16-alpine` image. Docker must be running and accessible to the current user. Testcontainers starts and stops the PostgreSQL container as part of the test execution; a separate Docker Compose setup is not required for these tests.

## 8. Common troubleshooting

### PostgreSQL connection failure

Check that:

- PostgreSQL is running.
- The database `cupcake` exists.
- The configured username and password are correct.
- `DB_USERNAME` and `DB_PASSWORD` are available to the application process.
- PostgreSQL is listening at `localhost:5432`, or adjust the development configuration to match the local environment.

### Hibernate schema validation failure

The application uses Hibernate schema validation rather than automatic schema creation in the normal configuration. Check that Flyway migrations have run and that the database schema matches the entity mappings.

### Testcontainers cannot start

Check that Docker is installed, running, and accessible to the user executing Maven. The integration tests require the `postgres:16-alpine` image to be available locally or downloadable by Docker.

### Missing database credentials

Set both `DB_USERNAME` and `DB_PASSWORD` in the environment used to launch the application. Do not place production credentials in the repository.

## 9. Useful Maven commands

| Command | Purpose |
|---|---|
| `mvn clean compile` | Clean and compile the application |
| `mvn clean test` | Clean and execute the tests |
| `mvn clean package` | Build the packaged application |
| `mvn spring-boot:run -Dspring-boot.run.profiles=dev` | Run with the development profile |
