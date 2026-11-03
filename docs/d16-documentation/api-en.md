# REST API Documentation

## 1. Overview

The Cupcake Unicsul backend exposes a REST API for product management and order processing.

Base path:

```text
/api/v1
```

The API uses:

- HTTP methods according to resource operations.
- JSON request and response bodies.
- Jakarta Validation for request validation.
- Spring Data pagination.
- HTTP status codes.
- ProblemDetail for error responses.

No authentication or authorization mechanism is currently implemented.

## 2. Product API

Base path:

```text
/api/v1/products
```

### 2.1 List active products

**GET** `/api/v1/products`

Returns a paginated list of active products.

Default pagination:

| Parameter | Default |
|---|---|
| page | 0 |
| size | 20 |
| sort | price |
| direction | ASC |

Example:

```http
GET /api/v1/products?page=0&size=10&sort=price,asc
```

Successful response:

`200 OK`

Example response:

```json
{
  "content": [
    {
      "id": 1,
      "name": "Chocolate",
      "description": "Chocolate cupcake",
      "price": 8.50,
      "imageUrl": null,
      "active": true
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "number": 0,
  "size": 10,
  "numberOfElements": 1,
  "empty": false
}
```

The example shows the main pagination fields. The actual Spring Data Page response may contain additional metadata.

Only active products are returned.

### 2.2 Get product by ID

**GET** `/api/v1/products/{id}`

Returns an active product.

Example:

```http
GET /api/v1/products/1
```

Successful response:

`200 OK`

Example:

```json
{
  "id": 1,
  "name": "Chocolate",
  "description": "Chocolate cupcake",
  "price": 8.50,
  "imageUrl": null,
  "active": true
}
```

Possible error:

- `404 Not Found`: product does not exist or is inactive.

### 2.3 Create product

**POST** `/api/v1/products`

Creates a new product.

Example request:

```json
{
  "name": "Vanilla",
  "description": "Vanilla cupcake",
  "price": 7.50,
  "imageUrl": null
}
```

Validation rules:

| Field | Rule |
|---|---|
| name | Required, maximum 150 characters |
| description | Required |
| price | Required, greater than zero, maximum 8 integer digits and 2 decimal places |
| imageUrl | Optional, maximum 500 characters |

Successful response:

`201 Created`

The response includes the created product and a `Location` header containing its resource URI.

### 2.4 Update product

**PUT** `/api/v1/products/{id}`

Updates product information.

Example:

```http
PUT /api/v1/products/1
```

Request body:

```json
{
  "name": "Vanilla Premium",
  "description": "Premium vanilla cupcake",
  "price": 9.50,
  "imageUrl": null
}
```

The request uses the same field validation rules as product creation.

Successful response:

`200 OK`

The updated product is returned.

Possible error:

- `404 Not Found`: product does not exist.

### 2.5 Deactivate product

**DELETE** `/api/v1/products/{id}`

Deactivates a product.

Example:

```http
DELETE /api/v1/products/1
```

Successful response:

`204 No Content`

This operation performs logical deactivation. The database record is not deleted.

Inactive products are excluded from the active product catalog and cannot be ordered.

## 3. Order API

Base path:

```text
/api/v1/orders
```

### 3.1 Create order

**POST** `/api/v1/orders`

Creates an order containing one or more products.

Example request:

```json
{
  "customerName": "John Smith",
  "customerEmail": "john@example.com",
  "deliveryAddress": "100 Main Street",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

Validation rules:

| Field | Rule |
|---|---|
| customerName | Required, maximum 150 characters |
| customerEmail | Required, valid email, maximum 254 characters |
| deliveryAddress | Required |
| items | Required, must contain at least one item |
| productId | Required, positive number |
| quantity | Required, positive number |

Business rules:

1. Every requested product must exist.
2. Inactive products cannot be ordered.
3. Repeated product identifiers are consolidated into a single order item.
4. Product prices are obtained from the database.
5. Client-provided prices are not accepted.
6. Subtotals and the order total are calculated by the backend.
7. Product name and unit price are stored as purchase-time snapshots.

Successful response:

`201 Created`

Example:

```json
{
  "id": 100,
  "customerName": "John Smith",
  "customerEmail": "john@example.com",
  "deliveryAddress": "100 Main Street",
  "createdAt": "2026-11-02T15:30:00Z",
  "total": 24.50,
  "items": [
    {
      "productId": 1,
      "productName": "Chocolate",
      "unitPrice": 8.50,
      "quantity": 2,
      "subtotal": 17.00
    },
    {
      "productId": 2,
      "productName": "Vanilla",
      "unitPrice": 7.50,
      "quantity": 1,
      "subtotal": 7.50
    }
  ]
}
```

The response includes a `Location` header pointing to the created order.

Possible errors:

| HTTP status | Description |
|---|---|
| 400 | Invalid request or validation failure |
| 404 | One or more products do not exist |
| 409 | Business rule violation, such as ordering an inactive product |

### 3.2 Get order by ID

**GET** `/api/v1/orders/{id}`

Returns an order and its items.

Example:

```http
GET /api/v1/orders/100
```

Successful response:

`200 OK`

The response follows the `OrderResponse` structure.

Possible error:

- `404 Not Found`: order does not exist.

### 3.3 List orders

**GET** `/api/v1/orders`

Returns a paginated list of orders.

Default pagination:

| Parameter | Default |
|---|---|
| page | 0 |
| size | 20 |
| sort | createdAt |
| direction | DESC |

Example:

```http
GET /api/v1/orders?page=0&size=10&sort=createdAt,desc
```

Successful response:

`200 OK`

The response uses Spring Data's paginated `Page<OrderResponse>` representation.

## 4. Error handling

The API uses Spring's `ProblemDetail` for standardized error responses.

The global exception handler is implemented by:

```text
shared/exception/GlobalExceptionHandler.java
```

### 4.1 HTTP status mapping

| Exception | HTTP status |
|---|---:|
| ResourceNotFoundException | 404 Not Found |
| BusinessRuleException | 409 Conflict |
| MethodArgumentNotValidException | 400 Bad Request |
| HttpMessageNotReadableException | 400 Bad Request |
| Unexpected exceptions | 500 Internal Server Error |

### 4.2 Validation error example

Example:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Um ou mais campos possuem valores inválidos.",
  "instance": "/api/v1/products",
  "errors": [
    {
      "field": "name",
      "message": "O nome do produto é obrigatório."
    },
    {
      "field": "price",
      "message": "O preço deve ser maior que zero."
    }
  ]
}
```

The `errors` property contains field-specific validation messages.

### 4.3 Unexpected errors

Unexpected exceptions return:

`500 Internal Server Error`

The response does not expose internal exception details.

The exception is recorded in the application log.

## 5. Pagination

Collection endpoints use Spring Data pagination.

Supported parameters include:

| Parameter | Description |
|---|---|
| page | Zero-based page number |
| size | Number of elements per page |
| sort | Property used for sorting; may include direction |

Examples:

```http
GET /api/v1/products?page=0&size=10
```

```http
GET /api/v1/orders?page=1&size=20&sort=createdAt,desc
```

Product listing defaults to ascending price.

Order listing defaults to descending creation date.

## 6. API security considerations

The current implementation does not include authentication or authorization.

Consequently:

- Product creation and modification endpoints are not protected by an authentication mechanism.
- Order listing is not restricted to administrators.
- Order lookup does not verify customer ownership.
- Order responses contain customer email and delivery address.

These are known limitations of the current academic implementation and should be considered before any production deployment.

## 7. Frontend integration

The planned frontend will consume the REST API using JavaScript's Fetch API.

The browser should:

- Request active products.
- Maintain the shopping cart locally.
- Submit product identifiers and quantities when creating orders.
- Display totals returned by the backend.
- Handle HTTP errors and validation messages.
- Avoid treating browser-calculated prices as authoritative.

The backend remains the source of truth for product prices and order totals.

## 8. Current API limitations

The current API does not provide:

- Customer registration or login.
- Payment processing.
- Order cancellation.
- Order status management.
- Product image upload.
- Product reactivation endpoint.
- Customer-specific order history.

Although the service layer contains a product activation operation, the current REST controller does not expose an endpoint for it.
