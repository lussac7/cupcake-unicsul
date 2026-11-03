# Database Documentation

## 1. Overview

The Cupcake project uses PostgreSQL as its database management system.

Data persistence is implemented using:

- PostgreSQL;
- Spring Data JPA;
- Hibernate;
- Flyway.

Hibernate provides object-relational mapping (ORM), while Flyway manages database schema evolution through versioned migrations.

The main configuration includes:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
```

Hibernate validates the compatibility between JPA entities and the existing database schema. Schema creation and evolution are managed by Flyway migrations.

## 2. Data Model

The database currently contains three main tables:

| Table | Responsibility |
|---|---|
| `products` | Stores the products available in the store |
| `orders` | Stores customer orders |
| `order_items` | Stores the products and quantities associated with each order |

### 2.1 Relationships

The relationships between the tables are:

- An order contains one or more order items.
- Each order item belongs to an order.
- Each order item references a registered product.

Simplified representation:

```text
products
   |
   | 1
   |
   | N
order_items
   |
   | N
   |
   | 1
orders
```

The `order_items` table represents the items associated with orders and preserves historical information about purchased products.

## 3. Table Structures

### 3.1 products Table

Stores the products sold by the store.

| Column | SQL Type | Description |
|---|---|---|
| `id` | As defined in the migration | Product identifier and primary key |
| `name` | VARCHAR(150) | Product name |
| `description` | TEXT | Product description |
| `price` | NUMERIC(10,2) | Product price |
| `image_url` | VARCHAR(500) | Optional image URL |
| `active` | BOOLEAN | Indicates whether the product is active |
| `created_at` | TIMESTAMPTZ | Creation date and time |
| `updated_at` | TIMESTAMPTZ | Last update date and time |

Constraints:

- Primary key on `id`.
- Name cannot be empty.
- Description cannot be empty.
- Price must be greater than zero.
- `active` has a default value of `TRUE`.

The `active` field allows products to be deactivated without physically deleting their database records.

### 3.2 orders Table

Stores customer orders.

| Column | SQL Type | Description |
|---|---|---|
| `id` | As defined in the migration | Order identifier and primary key |
| `customer_name` | VARCHAR(150) | Customer name |
| `customer_email` | VARCHAR(254) | Customer email |
| `delivery_address` | TEXT | Delivery address |
| `created_at` | TIMESTAMPTZ | Order creation date and time |
| `total` | NUMERIC(12,2) | Order total amount |

Constraints:

- Primary key on `id`.
- Customer name cannot be empty.
- Customer email cannot be empty.
- Delivery address cannot be empty.
- Order total must be greater than zero.

### 3.3 order_items Table

Stores the items associated with each order.

| Column | SQL Type | Description |
|---|---|---|
| `id` | As defined in the migration | Item identifier and primary key |
| `order_id` | As defined in the migration | Reference to the order |
| `product_id` | As defined in the migration | Reference to the product |
| `product_name` | VARCHAR(150) | Product name at purchase time |
| `unit_price` | NUMERIC(10,2) | Unit price at purchase time |
| `quantity` | INTEGER | Purchased quantity |
| `subtotal` | NUMERIC(12,2) | Item subtotal |

Constraints:

- Primary key on `id`.
- Foreign key `order_id` references `orders`.
- Foreign key `product_id` references `products`.
- Product name cannot be empty.
- Unit price must be greater than zero.
- Quantity must be greater than zero.
- Subtotal must equal unit price multiplied by quantity.
- The combination of `order_id` and `product_id` must be unique.

The foreign keys use `ON DELETE RESTRICT`, preventing the physical deletion of referenced records.

## 4. Purchase History Preservation

The `order_items` table stores the product name and unit price at the time of purchase.

This decision preserves the order history even when the original product record is updated later.

For example:

1. A product is sold for BRL 12.50.
2. A customer places an order containing that product.
3. The product price is subsequently changed to BRL 15.00.
4. The previous order continues to record the original price of BRL 12.50.

Therefore, the financial history of an order does not depend on the current product catalog values.

## 5. Indexes

The initial migration creates the following indexes:

| Index | Table | Purpose |
|---|---|---|
| `idx_products_active_name` | `products` | Optimize queries involving active products by name |
| `idx_order_items_product_id` | `order_items` | Optimize queries involving products associated with orders |
| `idx_orders_created_at` | `orders` | Optimize queries and sorting by order creation date |

The `idx_products_active_name` index is partial and includes only records where `active = TRUE`.

## 6. Data Integrity

The database implements constraints to protect data integrity.

The main rules include:

- Products must have valid names and descriptions.
- Product prices must be positive.
- Orders must contain valid customer information.
- Order totals must be positive.
- Order items must have positive quantities.
- Item subtotals must equal unit price multiplied by quantity.
- The same product cannot appear more than once in the same order.
- Referenced records cannot be physically deleted while references exist.

These constraints complement the validations performed by the application.

## 7. Flyway Database Migrations

Flyway manages database schema evolution.

Migration files are stored in:

```text
src/main/resources/db/migration/
```

The initial migration is:

```text
V1__initial_schema.sql
```

It creates the following tables:

- `products`;
- `orders`;
- `order_items`.

It also defines primary keys, foreign keys, integrity constraints, and indexes.

Future schema changes must be implemented through new versioned migrations. Migrations that have already been applied in shared or production environments should not be modified.

## 8. Environment Configuration

### 8.1 Development

The `dev` profile uses PostgreSQL.

Default connection:

```text
jdbc:postgresql://localhost:5432/cupcake
```

Credentials are obtained through environment variables:

- `DB_USERNAME`
- `DB_PASSWORD`

### 8.2 Tests

The `test` profile uses an in-memory H2 database.

Configuration:

```text
jdbc:h2:mem:cupcake_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1
```

In this profile:

- The database runs in memory.
- Hibernate uses `ddl-auto: create-drop`.
- Flyway is disabled.

### 8.3 Integration Tests

Integration tests use Testcontainers with the following image:

```text
postgres:16-alpine
```

This allows tests to run against a real PostgreSQL instance in a controlled environment, reducing differences between test behavior and the database used by the application.

## 9. Current Data Model Limitations

The current data model covers only products and orders.

There are no implemented tables for:

- Users and authentication.
- Addresses managed independently from orders.
- Persistent shopping carts.
- Payments.
- Product categories.
- Inventory.
- Order status or order change history.

These features must not be considered part of the current database schema.

## 10. Final Considerations

The current schema was designed to support the implemented product and order management operations.

Combining SQL constraints, application business rules, and versioned migrations helps preserve data integrity and enables controlled database evolution.

Any future changes to the data model must maintain consistency between:

- JPA entities.
- Repositories.
- Business rules.
- Flyway migrations.
- Automated tests.
- Technical documentation.
