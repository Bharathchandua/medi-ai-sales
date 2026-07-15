# Interview Notes

## Why I Started A New Project

My earlier project was a training project built with older JSP/Spring patterns. I started this new version to show modern backend development with Spring Boot REST APIs, cleaner layering, validation, exception handling, tests, and AI integration.

## Project Flow

```text
Client/Postman/React
        |
        v
Controller
        |
        v
Service
        |
        v
Repository
        |
        v
Database
```

## Product Module Explanation

The product module manages medical stock.

- `Product` is the JPA entity. It becomes a `products` table in the database.
- `ProductRequest` is the incoming request body. It contains validation rules.
- `ProductResponse` is the outgoing response. It prevents directly exposing the entity.
- `ProductRepository` extends `JpaRepository`, so Spring Data JPA creates common database methods automatically.
- `ProductService` contains business logic.
- `ProductController` exposes REST APIs under `/api/products`.

## Why DTOs Are Used

DTO means Data Transfer Object. I use DTOs because the API request/response should be separate from the database entity. This gives better security, validation, and flexibility.

## Why BigDecimal Is Used For Money

`BigDecimal` is used for price, GST, and discount because money calculations should avoid floating-point precision errors.

