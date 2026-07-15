# Day 1 Plan

Today is about understanding the backend foundation, not memorizing code.

## 1. Understand The Project Setup

Read these files:

- `pom.xml`
- `src/main/resources/application.properties`
- `src/main/java/com/medi/ai/sales/MediAiSalesApplication.java`

You should be able to say:

> This is a Spring Boot 3 project using Java 17. Maven manages dependencies. The app connects to MySQL using environment variables, so secrets are not hardcoded.

## 2. Understand The Product API Flow

Read files in this order:

1. `ProductController`
2. `ProductRequest`
3. `ProductService`
4. `Product`
5. `ProductRepository`
6. `ProductResponse`

Say this aloud:

> The client sends JSON to the controller. The controller validates it using ProductRequest. The service applies business logic. The repository saves the entity to the database. ProductResponse is returned to the client.

## 3. Learn These Interview Terms

- REST API
- DTO
- Entity
- Repository
- Service layer
- Dependency injection
- Validation
- Exception handling
- BigDecimal
- Environment variables

## 4. Practice Questions

Answer these in your own words:

1. Why did we create a new project instead of continuing the old one?
2. What is the difference between `Product` and `ProductRequest`?
3. Why do we use `JpaRepository`?
4. Why is business logic kept in the service?
5. Why should database passwords not be hardcoded?

## 5. Today Coding Task

After understanding Product, create one product API request in Postman later:

```json
{
  "name": "Paracetamol",
  "company": "Cipla",
  "batchNumber": "CP101",
  "hsnCode": "300490",
  "manufactureDate": "2026-01-01",
  "expiryDate": "2027-01-01",
  "quantity": 100,
  "rate": 12.50,
  "gstPercentage": 12,
  "discountPercentage": 5
}
```

